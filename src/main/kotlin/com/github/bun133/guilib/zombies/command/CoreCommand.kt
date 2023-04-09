package com.github.bun133.guilib.zombies.command

import com.github.bun133.guilib.zombies.Zombies
import net.kunmc.lab.commandlib.Command
import net.kunmc.lab.commandlib.CommandContext
import net.kunmc.lab.commandlib.argument.LocationArgument
import org.bukkit.Location

class CoreCommand(plugin: Zombies) : Command("core") {
    init {
        setDescription("Core Command")
        addChildren(CoreAddCommand(plugin), CoreRemoveCommand(plugin))
    }
}

class CoreAddCommand(plugin: Zombies) : Command("add") {
    init {
        setDescription("Add Core Command")
        argument(LocationArgument("coreBlock")) { loc: Location, ctx: CommandContext ->
            plugin.mConfig.coreLocationList.add(loc.toBlockLocation())
            ctx.sendSuccess("Coreを追加しました(${loc.toBlockLocation().block.type})")
        }
    }
}

class CoreRemoveCommand(plugin: Zombies) : Command("remove") {
    init {
        setDescription("Remove Core Command")
        argument(LocationArgument("coreBlock")) { loc: Location, ctx: CommandContext ->
            if (plugin.mConfig.coreLocationList.remove(loc.toBlockLocation())) {
                ctx.sendSuccess("Coreを削除しました")
            } else {
                ctx.sendFailure("Coreを削除できませんでした")
            }
        }
    }
}