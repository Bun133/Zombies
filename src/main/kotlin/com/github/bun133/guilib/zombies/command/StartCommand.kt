package com.github.bun133.guilib.zombies.command

import com.github.bun133.guilib.zombies.Zombies
import net.kunmc.lab.commandlib.Command

class StartCommand(plugin: Zombies) : Command("start") {
    init {
        setDescription("Start Game and Spawning and other logics")
        execute {
            plugin.spawn.startGame()
            it.sendSuccess("ゲームを開始しました")
        }
    }
}

class EndCommand(plugin: Zombies) : Command("end") {
    init {
        setDescription("End Game and Spawning and other logics")
        execute {
            plugin.config.isWaveStarted = false
            it.sendSuccess("ゲームを終了しました")
        }
    }
}