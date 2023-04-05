package com.github.bun133.guilib.zombies.trade

import com.github.bun133.guilib.zombies.Zombies
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Villager

class Trader(plugin: Zombies, location: Location) {
    val entity: Villager
    val trading = tradings

    init {
        // 村人だけ残ってるとき用
        location.world.getNearbyEntitiesByType(Villager::class.java, location, 0.5).forEach {
            it.remove()
        }
        entity = location.world.spawnEntity(location, EntityType.VILLAGER) as Villager
        entity.setAI(false)
        entity.removeWhenFarAway = false
        entity.profession = Villager.Profession.ARMORER

        plugin.trader.register(this)
    }
}