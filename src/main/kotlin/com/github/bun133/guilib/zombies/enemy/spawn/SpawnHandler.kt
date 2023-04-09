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
import kotlin.random.Random

class SpawnHandler(val plugin: Zombies) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private val enemies = mutableListOf<Pair<Enemy, LivingEntity>>()

    var targetSpawnCost: Double = plugin.config.initialSpawnCost.value()
        set(value) {
            field = value
            plugin.mConfig.lastTargetCost.value(value)
            plugin.logger.info("TargetSpawnCost Increased to ${value}")
        }

    /**
     * 場に出ている敵を一掃する
     */
    fun killAll() {
        enemies.forEach { (en, entity) ->
            entity.remove()
        }
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
            val chosen = randomPick(possible)
            target -= chosen.data.cost
            toSpawnEnemies.add(chosen)

            possible = Enemy.values().filter { it.data.cost <= target }
        }

        return toSpawnEnemies
    }

    private fun randomPick(possible: List<Enemy>): Enemy {
        val all = possible.sumOf { it.weight }
        val picked = all * Random.nextDouble()
        var currentIndex = 0
        var sum = possible[currentIndex].weight
        while (sum < picked) {
            currentIndex++
            sum += possible[currentIndex].weight
        }

        return possible[currentIndex]
    }


    private fun updateEntityList() {
        enemies.removeAll {
            !it.second.isValid
        }
    }

    // 現在アクティブなスポナー
    fun listActiveSpawner(): List<Location> {
        return plugin.mConfig.spawnerLocationList.value().toList()
    }

    @EventHandler
    private fun onSpawner(e: SpawnerSpawnEvent) {
        e.isCancelled = true
    }

    fun spawnEnemy(enemy: Enemy, loc: Location): LivingEntity {
        val enemyData = enemy.data
        val created = when (enemyData) {
            is EnemyData.Normal,
            is EnemyData.Simple -> {
                createEntity(loc, enemyData.entityType.entityClass!!) as EntityInsentient
            }
        }

        // setAI
        if (!plugin.ai.safeSetAI(created, enemy.data.ai)) {
            // Failed to Set AI
            plugin.logger.warning("Failed to Set AI,Entity:${created},Enemy AI:${enemy.data.ai}")
        }

        // Spawn
        val spawned = addEntity(created, loc.world) as LivingEntity
        // Marking
        Enemy.markDataTag(enemy, spawned)

        when (enemyData) {
            is EnemyData.Normal -> {
                handleNormal(enemyData, spawned)
            }

            is EnemyData.Simple -> {
                if (enemyData.itemClear) {
                    spawned.equipment!!.setItemInMainHand(null)
                    spawned.equipment!!.setItemInOffHand(null)
                }
            }
        }

        enemies.add(enemy to spawned)
        return spawned
    }

    private fun handleNormal(data: EnemyData.Normal, entity: LivingEntity): LivingEntity {
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = data.health
        entity.health = data.health

        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.baseValue = data.attack

        entity.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)!!.baseValue = data.defence

        entity.equipment!!.setItemInMainHand(null)
        entity.equipment!!.setItemInOffHand(null)

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
