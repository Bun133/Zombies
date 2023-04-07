package com.github.bun133.guilib.zombies.bossbar

import com.github.bun133.guilib.zombies.Zombies
import com.github.bun133.guilib.zombies.core.Core
import com.github.bun133.guilib.zombies.enemy.spawn.Wave
import org.bukkit.NamespacedKey
import org.bukkit.boss.*
import kotlin.math.max
import kotlin.math.min

class BossBarHandler(private val plugin: Zombies) {
    init {
        plugin.server.scheduler.runTaskTimer(plugin, Runnable { updateWaveBossBar();updateCoreBossBar() }, 20L, 20L)
    }

    private val waveBossBarKey = NamespacedKey(plugin, "bossbar")
    private fun getBossBar(key: NamespacedKey, default: String): KeyedBossBar {
        return plugin.server.getBossBar(key) ?: plugin.server.createBossBar(
            key,
            default,
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

    private fun updateWaveBossBar() {
        val bar = getBossBar(waveBossBarKey, "残りの敵")
        if (!plugin.isWaveStarted) {
            bar.isVisible = false
        } else {
            val wave = plugin.waver.wave
            when (wave) {
                is Wave.BeforeGame -> {
                    // Hide
                    bar.isVisible = false
                }

                is Wave.Attack -> {
                    val target = plugin.spawn.targetSpawnCost
                    val remain = plugin.spawn.getPresentCost()
                    val progress = min(1.0, max(remain / target, 0.0))
                    bar.setTitle("ウェーブ${wave.wave} 残りの敵")
                    bar.progress = progress
                    bar.isVisible = true
                    showToAll(bar)
                }

                is Wave.Prepare -> {
                    val remain = max(wave.durationTick - (plugin.server.currentTick - wave.startServerTime), 0)

                    val progress =
                        min(
                            1.0,
                            max(
                                remain.toDouble() / wave.durationTick.toDouble(),
                                0.0
                            )
                        )

                    bar.setTitle("準備フェーズ 残り${remain / 20}秒")
                    bar.progress = progress
                    bar.isVisible = true
                    showToAll(bar)
                }
            }

        }
    }

    private fun getCoreBossBarKey(core: Core): NamespacedKey {
        return NamespacedKey(plugin, "${core.blockLocation.hashCode()}")
    }

    private fun getCoreBossBar(core: Core): KeyedBossBar {
        return getBossBar(getCoreBossBarKey(core), "コア")
    }

    private fun updateCoreBossBar() {
        if (!plugin.isWaveStarted) {
            plugin.core.damages.forEach { (core, damage) ->
                val bar = getCoreBossBar(core)
                bar.isVisible = false
            }
        } else {
            plugin.core.damages.forEach { (core, damage) ->
                val bar = getCoreBossBar(core)
                bar.isVisible = true
                bar.progress = 1.0 - damage
                bar.setTitle("コア 耐久")
                showToAll(bar)
            }
        }
    }
}