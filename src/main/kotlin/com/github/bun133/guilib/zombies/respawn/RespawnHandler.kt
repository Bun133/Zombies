package com.github.bun133.guilib.zombies.respawn

import com.github.bun133.guilib.zombies.Zombies
import com.github.bun133.guilib.zombies.notice
import com.github.bun133.guilib.zombies.noticeWarn
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.entity.Player

class RespawnHandler(private val zombies: Zombies) {
    private val lastRespawned = mutableMapOf<Player, Int>()
    fun onRespawn(p: Player, respawnLocation: Location) {
        val last = lastRespawned[p]
        if (last != null) {
            val now = zombies.server.currentTick
            if (now - last >= zombies.config.respawnTime.value()) {
                // リスポーン可能
                doRespawn(p, respawnLocation)
            } else {
                // リスポーン不可能
                p.noticeWarn(
                    Component.text("リスポーンできません(次のリスポーンまであと${(zombies.config.respawnTime.value() - (now - last)) / 20}秒)")
                        .color(NamedTextColor.RED)
                )
            }
        } else {
            doRespawn(p, respawnLocation)
        }
    }

    private fun doRespawn(p: Player, respawnLocation: Location) {
        p.notice(Component.text("リスポーンしました").color(NamedTextColor.GREEN))
        lastRespawned[p] = zombies.server.currentTick
        p.teleport(respawnLocation)
    }
}