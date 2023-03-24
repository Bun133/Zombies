package com.github.bun133.guilib.zombies.command

import com.github.bun133.guilib.zombies.Zombies
import com.github.bun133.guilib.zombies.trade.Trader
import com.github.bun133.guilib.zombies.trade.tradings
import net.kunmc.lab.commandlib.Command
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent

class TraderCommand(val plugin: Zombies) : Command("trader"), Listener {
    init {
        execute {
            if (it.sender is Player) {
                players.add(it.sender as Player)
            }
        }

        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private val players = mutableListOf<Player>()

    @EventHandler
    fun onClick(e: PlayerInteractAtEntityEvent) {
        if (e.player in players && e.rightClicked.type == EntityType.VILLAGER) {
            // Register clicked entity as Trader
            Trader(plugin, e.rightClicked.location)
            e.rightClicked.remove()

            players.remove(e.player)

            e.player.sendMessage(Component.text("設定完了しました！").color(NamedTextColor.GREEN))
        }
    }
}