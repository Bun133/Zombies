package com.github.bun133.guilib.zombies.command

import com.github.bun133.guilib.zombies.Zombies
import net.kunmc.lab.commandlib.Command

class SpawnCommand(val plugin: Zombies) : Command("spawn") {
    init {
        setDescription("強制的にスポーンの処理を走らせます")
        execute {
            plugin.spawn.forceSpawn()
            it.sendSuccess("強制的にスポーン処理を行いました")
        }
    }
}