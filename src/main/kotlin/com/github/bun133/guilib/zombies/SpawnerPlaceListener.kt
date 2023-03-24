package com.github.bun133.guilib.zombies

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class SpawnerPlaceListener(val plugin: Zombies) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlace(e: BlockPlaceEvent) {
        if (e.blockPlaced.type == Material.SPAWNER) {
            plugin.config.spawnerLocationList.add(e.blockPlaced.location)
            e.player.sendMessage(Component.text("スポナーを登録しました"))
        }
    }
}