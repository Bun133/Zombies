package com.github.bun133.guilib.zombies.command

import com.github.bun133.guilib.zombies.Zombies
import net.kunmc.lab.commandlib.Command
import net.kunmc.lab.configlib.ConfigCommandBuilder

class ZombiesCommand(plugin: Zombies) : Command("zombies") {
    init {
        addChildren(
            ConfigCommandBuilder(plugin.config).build(),
            TraderCommand(plugin),
            SpawnCommand(plugin),
            WaveSetCommand(plugin),
            TargetLocationCommand(plugin),
            StartCommand(plugin)
        )
    }
}