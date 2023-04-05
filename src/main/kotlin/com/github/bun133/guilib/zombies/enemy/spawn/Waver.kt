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
        this.wave = Wave.BeforeGame()
        nextWave()
    }

    fun endGame() {
        plugin.isWaveStarted = false
        this.wave = Wave.BeforeGame()
    }

    var wave: Wave = Wave.BeforeGame()

    fun nextWave() {
        if (!plugin.isWaveStarted) return
        val next = wave.nextWave()
        wave.onEnd(this, next)
        wave = next
        wave.onStart(this)
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
    abstract fun nextWave(): Wave
    abstract fun waveCompleted(waver: Waver): Boolean

    class BeforeGame : Wave() {
        override fun onStart(waver: Waver) {
            waver.plugin.logger.info("Waver init")
        }

        override fun onEnd(waver: Waver, nextWave: Wave) {
            waver.plugin.logger.info("Starting Game: Start Wave $nextWave")
        }

        override fun nextWave(): Wave = Wave.Attack(1)
        override fun waveCompleted(waver: Waver): Boolean {
            return waver.plugin.isWaveStarted
        }
    }

    class Attack(val wave: Int) : Wave() {
        override fun onStart(waver: Waver) {
            waver.plugin.config.lastWaveCount.value(wave)
            waver.plugin.logger.info("New Wave:${wave} Started!")

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
            waver.plugin.logger.info("Wave$wave is end. Next Wave is $nextWave")
        }

        override fun nextWave(): Wave {
            return Prepare(20 * 30, wave + 1)
        }

        override fun waveCompleted(waver: Waver): Boolean {
            return waver.spawnHandler.getPresentCost() == 0.0
        }
    }

    class Prepare(val durationTick: Int, val waveTo: Int) : Wave() {
        var startServerTime: Int = Int.MAX_VALUE
        override fun onStart(waver: Waver) {
            waver.plugin.logger.info("Prepare Time Started! Next Wave is Wave:$waveTo")
            startServerTime = waver.plugin.server.currentTick
        }

        override fun onEnd(waver: Waver, nextWave: Wave) {
            waver.plugin.logger.info("Prepare Time is over! Next Wave is $nextWave")
        }

        override fun nextWave(): Wave = Attack(waveTo)
        override fun waveCompleted(waver: Waver): Boolean {
            return waver.plugin.server.currentTick >= startServerTime + durationTick
        }
    }
}