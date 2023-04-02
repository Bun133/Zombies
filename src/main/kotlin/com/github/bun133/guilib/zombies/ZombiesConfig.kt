package com.github.bun133.guilib.zombies

import net.kunmc.lab.configlib.BaseConfig
import net.kunmc.lab.configlib.value.DoubleValue
import net.kunmc.lab.configlib.value.IntegerValue
import net.kunmc.lab.configlib.value.LocationValue
import net.kunmc.lab.configlib.value.collection.LocationSetValue
import org.bukkit.plugin.java.JavaPlugin

class ZombiesConfig(plugin: JavaPlugin) : BaseConfig(plugin) {
    // TraderのLocationList
    val traderLocationList = LocationSetValue()

    // SpawnerのLocationList
    val spawnerLocationList = LocationSetValue()

    // Spawnerからこのブロック以内に人がいたらActiveとしてマークする
    val activeRange = DoubleValue(9.0)

    //. SpawnerがActiveになる最高光度
    val activeLight = IntegerValue(13)

    // BottomThresholdの初期値
    val initialThreshold = IntegerValue(10)

    // BottomThresholdの増加割合
    val multiplier = DoubleValue(1.01)

    // スポーンの頻度
    val spawnSec = IntegerValue(15)

    // Target Location
    val targetLocation = LocationValue()
}