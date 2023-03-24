package com.github.bun133.guilib.zombies

import net.kunmc.lab.configlib.BaseConfig
import net.kunmc.lab.configlib.value.BooleanValue
import org.bukkit.plugin.java.JavaPlugin

class ZombiesConfig(plugin: JavaPlugin) : BaseConfig(plugin) {
    // スポナーからスポーンするか否か
    val preventSpawnerSpawn = BooleanValue(true)
}