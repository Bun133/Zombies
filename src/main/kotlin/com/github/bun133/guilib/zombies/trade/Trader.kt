package com.github.bun133.guilib.zombies.trade

import com.github.bun133.guilib.zombies.Zombies
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Villager

class Trader(plugin: Zombies, location: Location, val tradings: List<Trading>) {
    val entity: Villager

    init {
        entity = location.world.spawnEntity(location, EntityType.VILLAGER) as Villager
        entity.setAI(false)
        entity.removeWhenFarAway = false

        plugin.trader.register(this)
    }
}