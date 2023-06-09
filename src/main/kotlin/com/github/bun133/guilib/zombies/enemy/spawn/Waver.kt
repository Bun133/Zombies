package com.github.bun133.guilib.zombies.enemy.spawn

import com.github.bun133.guilib.zombies.Zombies
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.scheduler.BukkitTask
import kotlin.math.pow

class Waver(internal val plugin: Zombies, internal val spawnHandler: SpawnHandler) {
    private lateinit var waveTask: BukkitTask

    init {
        fun setWaver() {
            waveTask = plugin.server.scheduler.runTaskTimer(
                plugin,
                Runnable {
                    checkNextWave()
                },
                0L,
                plugin.config.waveCheckInterval.value().toLong()
            )
        }
        setWaver()
        plugin.config.waveCheckInterval.onModify {
            waveTask.cancel()
            setWaver()
        }
    }

    fun startGame() {
        plugin.isWaveStarted = true
        plugin.core.resetCore()
        plugin.spawn.killAll()
        plugin.boss.resetBoss()
        this.wave = Wave.BeforeGame
        nextWave()
    }

    fun endGame() {
        plugin.isWaveStarted = false
        this.wave = Wave.BeforeGame
    }

    var wave: Wave = Wave.BeforeGame
        set(value) {
            field.onEnd(this, value)
            field = value
            value.onStart(this)
        }

    fun nextWave() {
        if (!plugin.isWaveStarted) return
        val next = wave.nextWave(this)
        wave = next
    }

    private fun checkNextWave() {
        if (!plugin.isWaveStarted) return
        if (wave.waveCompleted(this)) {
            nextWave()
        }
    }
}

sealed class Wave {
    abstract fun onStart(waver: Waver)
    abstract fun onEnd(waver: Waver, nextWave: Wave)
    abstract fun nextWave(waver: Waver): Wave
    abstract fun waveCompleted(waver: Waver): Boolean

    object BeforeGame : Wave() {
        override fun onStart(waver: Waver) {
        }

        override fun onEnd(waver: Waver, nextWave: Wave) {
            waver.plugin.logger.info("Starting Game Wave")
        }

        override fun nextWave(waver: Waver): Wave = Wave.Attack(1)
        override fun waveCompleted(waver: Waver): Boolean {
            return waver.plugin.isWaveStarted
        }
    }

    class Attack(val wave: Int) : Wave() {
        override fun onStart(waver: Waver) {
            waver.plugin.mConfig.lastWaveCount.value(wave)
            waver.plugin.logger.info("Wave${wave} Started!")

            waver.plugin.server.onlinePlayers.forEach {
                it.showTitle(
                    Title.title(
                        Component.text("WAVE $wave"),
                        Component.text("They're Coming!").color(NamedTextColor.DARK_RED)
                    )
                )
            }

            spawn(waver)
        }

        /**
         * スポーン処理
         */
        private fun spawn(waver: Waver) {
            if (waver.spawnHandler.listActiveSpawner().isNotEmpty()) {
                waver.spawnHandler.targetSpawnCost =
                    waver.plugin.config.initialSpawnCost.value() + waver.plugin.config.increaseFactor.value() * (wave - 1).toDouble()
                        .pow(waver.plugin.config.increaseFactor.value())
                val (isEnough, delta) = waver.spawnHandler.isEnemyEnough()
                if (!isEnough) {
                    // Delta分スポーンさせる
                    waver.spawnHandler.spawnNew(delta)
                }
            }
        }

        override fun onEnd(waver: Waver, nextWave: Wave) {
            waver.plugin.logger.info("Wave$wave is end")
        }

        override fun nextWave(waver: Waver): Wave {
            return Prepare(20 * 30, wave + 1)
        }

        override fun waveCompleted(waver: Waver): Boolean {
            return waver.spawnHandler.getPresentCost() == 0.0
        }
    }

    class Prepare(val durationTick: Int, val waveTo: Int) : Wave() {
        var startServerTime: Int = Int.MAX_VALUE
        override fun onStart(waver: Waver) {
            startServerTime = waver.plugin.server.currentTick
        }

        override fun onEnd(waver: Waver, nextWave: Wave) {
            waver.plugin.logger.info("Prepare Time is over!")
        }

        override fun nextWave(waver: Waver): Wave {
            return if (waver.plugin.config.bossWave.value() == waveTo) {
                BossWave
            } else {
                Attack(waveTo)
            }
        }

        override fun waveCompleted(waver: Waver): Boolean {
            return waver.plugin.server.currentTick >= startServerTime + durationTick
        }
    }

    object BossWave : Wave() {
        override fun onStart(waver: Waver) {
            waver.plugin.boss.startBossWave()
        }

        override fun onEnd(waver: Waver, nextWave: Wave) {
            waver.plugin.logger.info("BossWave End!")
        }

        override fun nextWave(waver: Waver): Wave {
            waver.endGame()
            return BeforeGame
        }

        override fun waveCompleted(waver: Waver): Boolean = waver.plugin.boss.bossWaveCompleted()
    }
}