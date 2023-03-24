package com.github.bun133.guilib.zombies

import net.kunmc.lab.commandlib.Command
import net.kunmc.lab.configlib.ConfigCommandBuilder

class ZombiesCommand(plugin: Zombies) : Command("zombies") {
    init {
        addChildren(ConfigCommandBuilder(plugin.config).build())
    }
}