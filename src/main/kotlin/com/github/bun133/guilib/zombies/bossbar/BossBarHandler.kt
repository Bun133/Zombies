package com.github.bun133.guilib.zombies.bossbar

import com.github.bun133.guilib.zombies.Zombies
import org.bukkit.NamespacedKey
import org.bukkit.boss.*
import kotlin.math.max
import kotlin.math.min

class BossBarHandler(private val plugin: Zombies) {
    init {
        plugin.server.scheduler.runTaskTimer(plugin, Runnable { updateBossBar() }, 20L, 20L)
    }

    private val nameKey = NamespacedKey(plugin, "bossbar")
    private fun getBossBar(): KeyedBossBar {
        return plugin.server.getBossBar(nameKey) ?: plugin.server.createBossBar(
            nameKey,
            "残りの敵",
            BarColor.WHITE,
            BarStyle.SEGMENTED_20,
            BarFlag.CREATE_FOG
        )
    }

    private fun showToAll(bar: BossBar) {
        (plugin.server.onlinePlayers - bar.players.toSet()).forEach {
            bar.addPlayer(it)
        }
    }

    private fun updateBossBar() {
        val bar = getBossBar()
        if (!plugin.config.isWaveStarted) {
            bar.isVisible = false
        } else {
            val wave = plugin.spawn.wave
            val target = plugin.spawn.targetSpawnCost
            val remain = plugin.spawn.getPresentCost()
            val progress = min(1.0, max(remain / target, 0.0))
            bar.setTitle("残りの敵 ウェーブ:${wave}")
            bar.progress = progress
            bar.isVisible = true
            showToAll(bar)
        }
    }
}