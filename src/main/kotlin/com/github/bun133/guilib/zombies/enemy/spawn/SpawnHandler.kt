package com.github.bun133.guilib.zombies.enemy.spawn

import com.github.bun133.guilib.zombies.Zombies
import com.github.bun133.guilib.zombies.enemy.Enemy
import com.github.bun133.guilib.zombies.enemy.EnemyData
import com.github.bun133.guilib.zombies.randomize
import net.minecraft.server.v1_16_R3.EntityInsentient
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.SpawnerSpawnEvent
import org.bukkit.util.Consumer

class SpawnHandler(val plugin: Zombies) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private val checker = SpawnChecker(plugin, this)
    private val enemies = mutableListOf<Pair<Enemy, LivingEntity>>()
    var wave: Int
        get() = checker.wave
        set(value) {
            checker.wave = value
        }

    /**
     * 強制的にスポーン処理を走らせる
     */
    fun forceSpawn() {
        checker.spawn()
    }

    var targetSpawnCost: Double = plugin.config.initialSpawnCost.value()
        set(value) {
            field = value
            plugin.config.lastTargetCost.value(value)
            plugin.logger.info("TargetSpawnCost Increased to ${value}")
        }

    /**
     * 場に出ている敵の合計Cost
     */
    internal fun getPresentCost(): Double {
        updateEntityList()
        return enemies.sumOf { it.first.data.cost }
    }


    /**
     * @return 場に出ている敵が[targetSpawnCost]と比較して充分かどうか,[targetSpawnCost]と現在の差額
     */
    internal fun isEnemyEnough(): Pair<Boolean, Double> {
        val presentCost = getPresentCost()
        val isEnough = targetSpawnCost <= presentCost
        val delta = targetSpawnCost - presentCost
        return isEnough to delta
    }


    /**
     * [toSpawn]分の敵をスポーンさせます
     */
    internal fun spawnNew(toSpawn: Double) {
        assert(toSpawn > 0.0)
        val spawnList = generateSpawnSet(toSpawn)

        // ActiveなSpawnerに順番にふりまいてスポーンさせる
        val activeSpawner = listActiveSpawner().shuffled()
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
    private fun generateSpawnSet(toSpawn: Double): List<Enemy> {
        var target = toSpawn
        val toSpawnEnemies = mutableListOf<Enemy>()

        var possible = Enemy.values().filter { it.data.cost <= target }
        while (possible.isNotEmpty()) {
            val chosen = possible.random()
            target -= chosen.data.cost
            toSpawnEnemies.add(chosen)

            possible = Enemy.values().filter { it.data.cost <= target }
        }

        return toSpawnEnemies
    }


    private fun updateEntityList() {
        enemies.removeAll {
            !it.second.isValid
        }
    }

    // 現在アクティブなスポナー
    fun listActiveSpawner(): List<Location> {
        return plugin.config.spawnerLocationList.filter {
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

        // setAI
        if (!plugin.ai.safeSetAI(created, enemy.ai)) {
            // Failed to Set AI
            plugin.logger.warning("Failed to Set AI,Entity:${created},Enemy AI:${enemy.ai}")
        }

        // Spawn
        val spawned = addEntity(created, loc.world)

        when (enemyData) {
            is EnemyData.Normal -> {
                handleNormal(enemyData, spawned)
            }
        }

        enemies.add(enemy to (spawned as LivingEntity))
    }

    private fun handleNormal(data: EnemyData.Normal, entity: Entity): LivingEntity {
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