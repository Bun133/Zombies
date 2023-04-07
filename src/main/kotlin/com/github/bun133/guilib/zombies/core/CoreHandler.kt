package com.github.bun133.guilib.zombies.core

import com.github.bun133.guilib.zombies.Zombies
import com.github.bun133.guilib.zombies.enemy.Enemy
import org.bukkit.Location

data class Core(
    val blockLocation: Location
)

class CoreHandler(val zombies: Zombies) {
    init {
        zombies.server.scheduler.runTaskTimer(zombies, Runnable { update() }, 0L, 20L)
    }


    val damages = mutableMapOf<Core, Float>()
    private val lastSendDamage = mutableMapOf<Core, Float>()

    init {
        zombies.config.coreLocationList.value().forEach {
            damages[Core(it)] = 0.0F
        }
        zombies.config.coreLocationList.onModify {
            val alreadyExist = damages.map { d -> d.key.blockLocation }
            it.filter { l -> l !in alreadyExist }.forEach { l ->
                damages[Core(l)] = 0.0F
            }
        }
    }

    private fun update() {
        damages.keys.forEach { c ->
            val nearBy = c.blockLocation.getNearbyEntities(
                zombies.config.coreBreakRange.value(),
                zombies.config.coreBreakRange.value(),
                zombies.config.coreBreakRange.value()
            )

            val costSum = nearBy.sumOf { Enemy.inferEnemy(it)?.data?.cost ?: 0.0 }
            val addDamage = costSum.toFloat() * 0.001F // TODO Config

            damages[c] = damages[c]!! + addDamage

            if (damages[c]!! >= 1.0) {
                // TODO 破壊・敗北処理
            }
        }

        updateBlockBreak()
    }

    private fun updateBlockBreak() {
        val toSend = mutableMapOf<Core, Float>()
        damages.forEach { (core, damage) ->
            val last = lastSendDamage[core] ?: 0.0
            if (last != damage) toSend[core] = damage
        }

        toSend.forEach { (core, damage) ->
            zombies.server.onlinePlayers.forEach {
                it.sendBlockDamage(core.blockLocation, damage)
            }
            lastSendDamage[core] = damage
        }
    }
}