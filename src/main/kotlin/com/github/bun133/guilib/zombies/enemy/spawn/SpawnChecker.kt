package com.github.bun133.guilib.zombies.enemy.spawn

import com.github.bun133.guilib.zombies.Zombies
import org.bukkit.scheduler.BukkitTask
import java.lang.Math.pow

class SpawnChecker(private val plugin: Zombies, private val spawnHandler: SpawnHandler) {
    private lateinit var waveTask: BukkitTask

    init {
        fun setWaver() {
            waveTask = plugin.server.scheduler.runTaskTimer(
                plugin,
                Runnable {
                    wave()
                },
                0L,
                plugin.config.waveInterval.value().toLong()
            )
        }
        setWaver()
        plugin.config.waveInterval.onModify {
            waveTask.cancel()
            setWaver()
        }
    }

    internal fun spawn() {
        if (!plugin.config.isWaveStarted.value()) return
        calTargetSpawnCost()
        val (isEnough, delta) = spawnHandler.isEnemyEnough()
        if (!isEnough) {
            // Delta分スポーンさせる
            spawnHandler.spawnNew(delta)
        }
    }

    var wave = 0
        set(value) {
            field = value
            plugin.config.lastWaveCount.value(wave)
            plugin.logger.info("New Wave${wave} Started!")
        }

    private fun wave() {
        if (!plugin.config.isWaveStarted.value()) return
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
}