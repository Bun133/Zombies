package com.github.bun133.guilib.zombies.command

import com.github.bun133.guilib.zombies.Zombies
import net.kunmc.lab.commandlib.Command
import net.kunmc.lab.commandlib.CommandContext
import net.kunmc.lab.commandlib.argument.LocationArgument
import org.bukkit.Location
import org.bukkit.Material

class CoreSetCommand(plugin: Zombies) : Command("core") {
    init {
        setDescription("Set Core at Inputted Location")
        argument(LocationArgument("coreBlock")) { loc: Location, ctx: CommandContext ->
            if (loc.block.type == Material.AIR) {
                ctx.sendFailure("ブロックを指定してください")
            } else {
                plugin.config.coreLocationList.clear()
                plugin.config.coreLocationList.add(loc)
                ctx.sendSuccess("Coreを変更しました")
            }
        }
    }
}