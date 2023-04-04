package com.github.bun133.guilib.zombies.enemy.spawn

import com.github.bun133.guilib.zombies.Zombies
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.scheduler.BukkitTask
import java.lang.Math.pow

class SpawnChecker(private val plugin: Zombies, private val spawnHandler: SpawnHandler) {
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
        plugin.config.isWaveStarted = true
        this.wave = 0
        nextWave()
    }

    private fun spawn() {
        if (!plugin.config.isWaveStarted) return
        calTargetSpawnCost()
        val (isEnough, delta) = spawnHandler.isEnemyEnough()
        if (!isEnough) {
            // Delta分スポーンさせる
            spawnHandler.spawnNew(delta)
        }
    }

    var wave = 0
        private set(value) {
            field = value
            plugin.config.lastWaveCount.value(wave)
            plugin.logger.info("New Wave${wave} Started!")

            plugin.server.onlinePlayers.forEach {
                it.showTitle(
                    Title.title(
                        Component.text("WAVE $value"),
                        Component.text("They're Coming!").color(NamedTextColor.DARK_RED)
                    )
                )
            }
        }

    fun nextWave() {
        if (!plugin.config.isWaveStarted) return
        wave++
        if (spawnHandler.listActiveSpawner().isNotEmpty()) {
            // TODO Log使う
            calTargetSpawnCost()
            spawn()
        }
    }

    private fun calTargetSpawnCost() {
        spawnHandler.targetSpawnCost =
            plugin.config.initialSpawnCost.value() * pow(plugin.config.increaseFactor.value(), wave.toDouble())
    }

    private fun checkNextWave() {
        if (!plugin.config.isWaveStarted) return
        if (spawnHandler.getPresentCost() == 0.0) {
            nextWave()
        }
    }
}