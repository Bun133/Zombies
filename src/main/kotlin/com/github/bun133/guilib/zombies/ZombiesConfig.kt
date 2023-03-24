package com.github.bun133.guilib.zombies

import net.kunmc.lab.configlib.BaseConfig
import net.kunmc.lab.configlib.value.DoubleValue
import net.kunmc.lab.configlib.value.collection.LocationSetValue
import org.bukkit.plugin.java.JavaPlugin

class ZombiesConfig(plugin: JavaPlugin) : BaseConfig(plugin) {
    // TraderのLocationList
    val traderLocationList = LocationSetValue()

    // SpawnerのLocationList
    val spawnerLocationList = LocationSetValue()

    // Spawnerからこのブロック以内に人がいたらActiveとしてマークする
    val activeRange = DoubleValue(9.0)
}