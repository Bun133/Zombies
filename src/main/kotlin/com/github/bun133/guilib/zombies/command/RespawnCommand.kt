package com.github.bun133.guilib.zombies.command

import com.github.bun133.guilib.zombies.Zombies
import net.kunmc.lab.commandlib.Command
import net.kunmc.lab.commandlib.CommandContext
import net.kunmc.lab.commandlib.argument.LocationArgument
import net.kunmc.lab.commandlib.argument.PlayerArgument
import org.bukkit.Location
import org.bukkit.entity.Player

class RespawnCommand(plugin: Zombies) : Command("respawn") {
    init {
        setDescription("Respawn Command")
        argument(
            PlayerArgument("respawnedPlayer"),
            LocationArgument("respawnLocation")
        ) { p: Player, l: Location, ctx: CommandContext ->
            plugin.respawn.onRespawn(p, l)
        }
    }
}