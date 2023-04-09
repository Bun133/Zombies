package com.github.bun133.guilib.zombies.enemy.boss

import com.github.bun133.guilib.zombies.Zombies
import org.bukkit.entity.LivingEntity

class BossHandler(val zombies: Zombies) {
    init {
        zombies.server.scheduler.runTaskTimer(zombies, Runnable { checkBoss() }, 0L, 20L)
    }

    private var isCompleted = false
    var currentBossType: Boss? = null
        private set
    var bossEntity: LivingEntity? = null
        private set

    fun startBossWave() {
        isCompleted = false
        spawnBoss()
    }

    private fun spawnBoss() {
        val spawner = zombies.spawn.listActiveSpawner().randomOrNull()
        if (spawner == null) {
            zombies.logger.severe("[BossWave]Spawner Not Found!")
            return
        }

        val boss = Boss.values().randomOrNull()!!

        currentBossType = boss
        bossEntity = zombies.spawn.spawnEnemy(boss.enemy, spawner.toBlockLocation())

        zombies.logger.info("Boss Spawned")
    }

    private fun checkBoss() {
        if (currentBossType != null) {
            if (bossEntity != null && !bossEntity!!.isValid) {
                // Boss Dead
                isCompleted = true
                bossEntity = null
                currentBossType = null
                // Players win!
                zombies.onWin()
            }
        }
    }

    fun bossWaveCompleted() = isCompleted
}