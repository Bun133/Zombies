package com.github.bun133.guilib.zombies.enemy

import com.github.bun133.guilib.zombies.Zombies
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.SpawnerSpawnEvent

class SpawnHandler(val plugin: Zombies) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun onSpawner(e: SpawnerSpawnEvent) {
        if (plugin.config.preventSpawnerSpawn.value()) {
                plugin.logger.info("onSpawner:Cancelling")
            e.isCancelled = true
        } else {
            handleSpawner(e)
        }
    }

    private fun handleSpawner(e: SpawnerSpawnEvent) {
        val enemy = Enemy.inferEnemy(e.entity)
        if (enemy != null) {
            // Handle Spawn
            e.isCancelled = true
            spawnEnemy(enemy, e.location)
        } else {
            // Leave it as it is
        }
    }

    fun spawnEnemy(enemy: Enemy, loc: Location) {
        val enemyData = enemy.data
        val spawned = when (enemyData) {
            is EnemyData.Normal -> {
                handleNormal(enemyData, loc)
            }
        }

        plugin.ai.setAI(spawned)
    }

    private fun handleNormal(data: EnemyData.Normal, loc: Location): LivingEntity {
        plugin.logger.info("handleNormal")
        val entity = loc.world.spawnEntity(loc, data.entityType)
        if (entity !is LivingEntity) throw Error("#handleNormal is not able to handle spawning non-living entity")

        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = data.health
        entity.health = data.health

        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.baseValue = data.attack

        entity.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)!!.baseValue = data.defence

        return entity
    }
}
