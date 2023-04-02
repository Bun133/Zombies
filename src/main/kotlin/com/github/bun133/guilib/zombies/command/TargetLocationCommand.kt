package com.github.bun133.guilib.zombies.command

import com.github.bun133.guilib.zombies.Zombies
import net.kunmc.lab.commandlib.Command
import net.kunmc.lab.commandlib.CommandContext
import net.kunmc.lab.commandlib.argument.LocationArgument
import org.bukkit.Location

class TargetLocationCommand(plugin: Zombies) : Command("target") {
    init {
        setDescription("Set Target Location at Inputted Location")
        argument(LocationArgument("targetLocation")) { loc: Location, ctx: CommandContext ->
            plugin.config.targetLocation.value(loc)
            ctx.sendSuccess("Targetを変更しました")
        }
    }
}