package com.github.bun133.guilib.zombies.command

import com.github.bun133.guilib.zombies.Zombies
import com.github.bun133.guilib.zombies.enemy.spawn.Wave
import net.kunmc.lab.commandlib.Command

class BossCommand(plugin: Zombies) : Command("boss") {
    init {
        setDescription("For Debug,Call Boss Wave Instantly")
        execute {
            plugin.waver.wave = Wave.BossWave
            it.sendSuccess("ボスウェーブに設定しました")
        }
    }
}