package com.github.bun133.guilib.zombies

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class SpawnerPlaceListener(val plugin: Zombies) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlace(e: BlockPlaceEvent) {
        if (e.blockPlaced.type == Material.SPAWNER) {
            plugin.mConfig.spawnerLocationList.add(e.blockPlaced.location)
            e.player.notice(Component.text("スポナーを登録しました"))
        }
    }

    @EventHandler
    fun onBreak(e: BlockBreakEvent) {
        if (e.block.type == Material.SPAWNER) {
            plugin.mConfig.spawnerLocationList.remove(e.block.location)
            e.player.notice(Component.text("スポナーを登録解除しました"))
        }
    }
}