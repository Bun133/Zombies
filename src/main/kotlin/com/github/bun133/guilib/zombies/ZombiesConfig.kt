package com.github.bun133.guilib.zombies

import net.kunmc.lab.configlib.BaseConfig
import net.kunmc.lab.configlib.value.BooleanValue
import net.kunmc.lab.configlib.value.DoubleValue
import net.kunmc.lab.configlib.value.IntegerValue
import net.kunmc.lab.configlib.value.LocationValue
import net.kunmc.lab.configlib.value.collection.LocationSetValue
import org.bukkit.plugin.java.JavaPlugin

class ZombiesConfig(plugin: JavaPlugin) : BaseConfig(plugin) {
    // is Game Started
    val isWaveStarted = BooleanValue(false)

    // TraderのLocationList
    val traderLocationList = LocationSetValue()

    // SpawnerのLocationList
    val spawnerLocationList = LocationSetValue()

    //. SpawnerがActiveになる最高光度
    val activeLight = IntegerValue(13)

    // SpawnCostの初期値
    val initialSpawnCost = DoubleValue(10.0)

    // Waveの間隔
    // 次のWaveはTargetSpawnCostを[increaseFactor]倍される
    val waveInterval = IntegerValue(20 * 60)
    val increaseFactor = DoubleValue(1.1)

    // 最終時点でのTargetCostの値(復旧用)
    // なお、自動復旧機能はありません、コマンドをご利用ください
    val lastTargetCost = DoubleValue(10.0)
    val lastWaveCount = IntegerValue(0)

    // Target Location
    val targetLocation = LocationValue()
}