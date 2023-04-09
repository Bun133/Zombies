package com.github.bun133.guilib.zombies

import net.kunmc.lab.configlib.BaseConfig
import net.kunmc.lab.configlib.value.DoubleValue
import net.kunmc.lab.configlib.value.FloatValue
import net.kunmc.lab.configlib.value.IntegerValue
import net.kunmc.lab.configlib.value.LocationValue
import net.kunmc.lab.configlib.value.collection.LocationSetValue
import org.bukkit.plugin.java.JavaPlugin

class ZombiesConfig(plugin: JavaPlugin) : BaseConfig(plugin) {

    // TraderのLocationList
    val traderLocationList = LocationSetValue()

    // SpawnerのLocationList
    val spawnerLocationList = LocationSetValue()

    // CoreのLocationList
    val coreLocationList = LocationSetValue()

    // Coreを破壊できる範囲
    val coreBreakRange = DoubleValue(1.5)

    // Coreにダメージを入れる割合
    // (1秒に一回)
    // Coreから[coreBreakRange]内にいる敵のCost合計 * この値 のダメージをコアに与えます
    val coreBreakRate = FloatValue(0.001F)

    // SpawnCostの初期値
    val initialSpawnCost = DoubleValue(10.0)

    // WaveCheckの間隔
    // 次のWaveはTargetSpawnCostを[increaseFactor]倍される
    val waveCheckInterval = IntegerValue(20 * 1)
    val increaseFactor = DoubleValue(1.1)

    // 最終時点でのTargetCostの値(復旧用)
    // なお、自動復旧機能はありません、コマンドをご利用ください
    val lastTargetCost = DoubleValue(10.0)
    val lastWaveCount = IntegerValue(0)
}