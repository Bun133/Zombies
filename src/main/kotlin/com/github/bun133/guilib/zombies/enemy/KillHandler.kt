package com.github.bun133.guilib.zombies.enemy

import com.github.bun133.guilib.zombies.Zombies
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class KillHandler(plugin: Zombies) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }


    @EventHandler
    fun onEnemyDeath(e: EntityDeathEvent) {
        if (e !is Player) {
            val enemy = Enemy.inferEnemy(e.entity)
            if (enemy != null) {
                // Drop Reward
                handleDeath(e, enemy)
            } else {
                // Leave it as it is
            }
        }
    }

    private fun handleDeath(e: EntityDeathEvent, enemy: Enemy) {
        val enemyData = enemy.data
        when (enemyData) {
            is EnemyData.Normal -> {
                e.droppedExp = enemyData.reward
            }
        }
    }
}