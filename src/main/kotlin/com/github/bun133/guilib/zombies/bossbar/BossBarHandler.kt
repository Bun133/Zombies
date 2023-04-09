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
    private val bossBossBarKey = NamespacedKey(plugin, "boss")
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
        val waveBar = getBossBar(waveBossBarKey, "残りの敵")
        val bossBar = getBossBar(bossBossBarKey, "ボスの体力")

        if (!plugin.isWaveStarted) {
            waveBar.isVisible = false
            bossBar.isVisible = false
        } else {
            val wave = plugin.waver.wave
            when (wave) {
                is Wave.BeforeGame -> {
                    // Hide
                    waveBar.isVisible = false
                    bossBar.isVisible = false
                }

                is Wave.Attack -> {
                    bossBar.isVisible = false

                    val target = plugin.spawn.targetSpawnCost
                    val remain = plugin.spawn.getPresentCost()
                    val progress = min(1.0, max(remain / target, 0.0))
                    waveBar.setTitle("ウェーブ${wave.wave} 残りの敵")
                    waveBar.progress = progress
                    waveBar.isVisible = true
                    showToAll(waveBar)
                }

                is Wave.Prepare -> {
                    bossBar.isVisible = false

                    val remain = max(wave.durationTick - (plugin.server.currentTick - wave.startServerTime), 0)

                    val progress =
                        min(
                            1.0,
                            max(
                                remain.toDouble() / wave.durationTick.toDouble(),
                                0.0
                            )
                        )

                    waveBar.setTitle("準備フェーズ 残り${remain / 20}秒")
                    waveBar.progress = progress
                    waveBar.isVisible = true
                    showToAll(waveBar)
                }

                is Wave.BossWave -> {
                    waveBar.isVisible = false

                    val e = plugin.boss.bossEntity
                    if (e != null) {
                        bossBar.isVisible = true
                        showToAll(bossBar)
                        val progress =
                            e.health / e.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH)!!.value
                        bossBar.progress = progress
                        if (plugin.boss.currentBossType != null) {
                            bossBar.setTitle("${plugin.boss.currentBossType!!.data.displayName}の体力")
                            bossBar.color = plugin.boss.currentBossType!!.data.bossBarColor
                        } else {
                            bossBar.color = BarColor.WHITE
                        }
                    } else {
                        bossBar.isVisible = false
                    }
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