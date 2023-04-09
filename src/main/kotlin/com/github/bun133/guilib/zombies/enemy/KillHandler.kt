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
        when (val enemyData = enemy.data) {
            is EnemyData.Normal -> {
                e.droppedExp = enemyData.reward
                e.drops.clear()
            }

            is EnemyData.Simple -> {
                e.droppedExp = enemyData.dropExp
                e.drops.clear()
            }

            is EnemyData.Boss -> {
                e.droppedExp = 0
                e.drops.clear()
            }
        }
    }
}