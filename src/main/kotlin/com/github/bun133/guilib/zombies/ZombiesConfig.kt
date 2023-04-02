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
    // [Disabled]
    val activeRange = DoubleValue(9.0)

    //. SpawnerがActiveになる最高光度
    val activeLight = IntegerValue(13)

    // 現在のSpawnCostに合わせてスポーン処理を走らせる間隔
    val spawnCheckInterval = IntegerValue(20 * 1)

    // SpawnCostの初期値
    val initialSpawnCost = DoubleValue(10.0)

    // SpawnCostを[increaseFactor]倍する間隔Tick
    val increaseIntervalTick = IntegerValue(20 * 1)
    val increaseFactor = DoubleValue(1.01)

    // 最終時点でのTargetCostの値(復旧用)
    // なお、自動復旧機能はありません、コマンドをご利用ください
    val lastTargetCost = DoubleValue(10.0)

    // Target Location
    val targetLocation = LocationValue()
}