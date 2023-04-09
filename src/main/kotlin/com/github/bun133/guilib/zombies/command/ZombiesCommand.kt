package com.github.bun133.guilib.zombies.command

import com.github.bun133.guilib.zombies.Zombies
import net.kunmc.lab.commandlib.Command
import net.kunmc.lab.configlib.ConfigCommandBuilder

class ZombiesCommand(plugin: Zombies) : Command("zombies") {
    init {
        addChildren(
            ConfigCommandBuilder(plugin.config).addConfig(plugin.mConfig).build(),
            TraderCommand(plugin),
            NextWaveCommand(plugin),
            CoreCommand(plugin),
            StartCommand(plugin),
            EndCommand(plugin)
        )
    }
}