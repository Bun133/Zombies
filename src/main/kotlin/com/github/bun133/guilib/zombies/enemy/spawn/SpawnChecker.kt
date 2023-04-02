package com.github.bun133.guilib.zombies.enemy.spawn

import com.github.bun133.guilib.zombies.Zombies
import org.bukkit.scheduler.BukkitTask

class SpawnChecker(private val plugin: Zombies, private val spawnHandler: SpawnHandler) {
    private lateinit var checkTask: BukkitTask
    private lateinit var increaseTask: BukkitTask

    init {
        fun setChecker() {
            checkTask = plugin.server.scheduler.runTaskTimer(
                plugin,
                Runnable {
                    check()
                },
                0L,
                plugin.config.spawnCheckInterval.value().toLong()
            )
        }
        setChecker()
        plugin.config.spawnCheckInterval.onModify {
            checkTask.cancel()
            setChecker()
        }

        fun setIncreaser() {
            increaseTask = plugin.server.scheduler.runTaskTimer(
                plugin,
                Runnable {
                    increase()
                },
                0L,
                plugin.config.increaseIntervalTick.value().toLong()
            )
        }
        setIncreaser()
        plugin.config.increaseIntervalTick.onModify {
            increaseTask.cancel()
            setIncreaser()
        }
    }

    private fun check() {
        val (isEnough, delta) = spawnHandler.isEnemyEnough()
        if (!isEnough) {
            // Delta分スポーンさせる
            spawnHandler.spawnNew(delta)
        }
    }

    private fun increase() {
        if(spawnHandler.listActiveSpawner().isNotEmpty()){
            spawnHandler.targetSpawnCost *= plugin.config.increaseFactor.value()
            plugin.config.lastTargetCost.value(spawnHandler.targetSpawnCost)
            plugin.logger.info("TargetSpawnCost Increased to ${spawnHandler.targetSpawnCost}")
        }
    }
}