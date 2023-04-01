package com.github.bun133.guilib.zombies.enemy

import com.github.bun133.guilib.zombies.Zombies
import com.github.bun133.guilib.zombies.randomize
import net.minecraft.server.v1_16_R3.EntityInsentient
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.SpawnerSpawnEvent
import org.bukkit.util.Consumer
import kotlin.math.log10
import kotlin.math.pow

class SpawnHandler(val plugin: Zombies) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
        plugin.server.scheduler.runTaskTimer(plugin, Runnable { sec() }, 0L, 20L * plugin.config.spawnSec.value())
    }

    private val entities = mutableListOf<Pair<Enemy, LivingEntity>>()

    fun forceUpdateBottomThreshold(new: Int) {
        bottomThreshold = new
    }

    // remainCostがこの値を下回ったら、新しくスポーンさせる
    private var bottomThreshold = plugin.config.initialThreshold.value()


    fun forceFireTick(){
        sec()
    }
    /**
     * Tick関数(20Tickおき)
     */
    private fun sec() {
        if (listActiveSpawner().isEmpty()) {
            plugin.logger.info("No Active Spawner!")
            return
        }
        updateEntityList()
        val remainCost = entities.map { it.first }.sumOf { it.data.cost }
        if (remainCost < bottomThreshold) {
            plugin.logger.info("Spawning! Remain:${remainCost} Threshold:${bottomThreshold}")
            spawnNew(remainCost)
            updateBottomThreshold()
        }
    }

    private fun updateBottomThreshold() {
        bottomThreshold = (bottomThreshold * plugin.config.multiplier.value()).toInt()
    }


    private fun spawnNew(remainCost: Int) {
        // このスポーンでどこまでスポーンするか
        val target = bottomThreshold * Math.E.pow(log10(bottomThreshold / 10.0))
        val toSpawn = (target - remainCost).toInt()
        assert(toSpawn > 0)
        val spawnList = generateSpawnSet(toSpawn)
        val activeSpawner = listActiveSpawner()
        var spawnerIndex = 0

        spawnList.forEach {
            if (spawnerIndex !in activeSpawner.indices) {
                spawnerIndex = 0
            }

            val spawner = activeSpawner.getOrNull(spawnerIndex)
            if (spawner != null) {
                spawnEnemy(it, spawner.randomize(1.5))
            }
            spawnerIndex++
        }
    }

    // TODO 改善
    private fun generateSpawnSet(toSpawn: Int): List<Enemy> {
        var target = toSpawn
        val toSpawnEnemies = mutableListOf<Enemy>()

        var possible = Enemy.values().filter { it.data.cost <= target }
        do {
            val chosen = possible.random()
            target -= chosen.data.cost
            toSpawnEnemies.add(chosen)

            possible = Enemy.values().filter { it.data.cost <= target }
        } while (possible.isNotEmpty())

        return toSpawnEnemies
    }


    private fun updateEntityList() {
        entities.removeAll {
            !it.second.isValid
        }
    }

    // 現在アクティブなスポナー
    private fun listActiveSpawner(): List<Location> {
        return plugin.config.spawnerLocationList.filter {
            it.getNearbyEntitiesByType(
                Player::class.java,
                plugin.config.activeRange.value()
            ).isNotEmpty() &&
                    it.block.lightLevel <= plugin.config.activeLight.value()
        }
    }

    @EventHandler
    private fun onSpawner(e: SpawnerSpawnEvent) {
        e.isCancelled = true
    }

    fun spawnEnemy(enemy: Enemy, loc: Location) {
        val enemyData = enemy.data
        val created = when (enemyData) {
            is EnemyData.Normal -> {
                createEntity(loc, enemyData.entityType.entityClass!!) as EntityInsentient
            }
        }

        // Spawn
        val spawned = addEntity(created, loc.world)

        when (enemyData) {
            is EnemyData.Normal -> {
                handleNormal(enemyData, spawned)
            }
        }

        entities.add(enemy to (spawned as LivingEntity))
    }

    private fun handleNormal(data: EnemyData.Normal, entity: Entity): LivingEntity {
        plugin.logger.info("handleNormal")
        if (entity !is LivingEntity) throw Error("#handleNormal is not able to handle spawning non-living entity")

        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = data.health
        entity.health = data.health

        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.baseValue = data.attack

        entity.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)!!.baseValue = data.defence

        return entity
    }

    private fun <E : Entity> createEntity(loc: Location, clazz: Class<E>): net.minecraft.server.v1_16_R3.Entity {
        val createEntity = CraftWorld::class.java.getMethod("createEntity", Location::class.java, Class::class.java)
        createEntity.isAccessible = true
        return createEntity.invoke(loc.world, loc, clazz) as net.minecraft.server.v1_16_R3.Entity
    }

    private fun addEntity(entity: net.minecraft.server.v1_16_R3.Entity, world: World): org.bukkit.entity.Entity {
        val addEntity = CraftWorld::class.java.getMethod(
            "addEntity",
            net.minecraft.server.v1_16_R3.Entity::class.java,
            CreatureSpawnEvent.SpawnReason::class.java,
            Consumer::class.java
        )
        addEntity.isAccessible = true
        return addEntity.invoke(
            world,
            entity,
            CreatureSpawnEvent.SpawnReason.CUSTOM,
            Consumer<Entity> {}) as org.bukkit.entity.Entity
    }
}
