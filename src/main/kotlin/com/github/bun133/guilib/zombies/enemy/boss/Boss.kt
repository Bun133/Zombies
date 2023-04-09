package com.github.bun133.guilib.zombies.enemy.boss

import com.github.bun133.guilib.zombies.enemy.Enemy
import org.bukkit.boss.BarColor

data class BossData(
    val displayName: String,
    val bossBarColor: BarColor
)

enum class Boss(val data: BossData, val enemy: Enemy) {
    WITHER(BossData("WITHER", BarColor.RED), Enemy.Wither)
}