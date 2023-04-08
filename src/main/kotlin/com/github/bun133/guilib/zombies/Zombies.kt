package com.github.bun133.guilib.zombies

import com.github.bun133.guilib.zombies.bossbar.BossBarHandler
import com.github.bun133.guilib.zombies.command.ZombiesCommand
import com.github.bun133.guilib.zombies.core.CoreHandler
import com.github.bun133.guilib.zombies.enemy.AIHandler
import com.github.bun133.guilib.zombies.enemy.KillHandler
import com.github.bun133.guilib.zombies.enemy.spawn.SpawnHandler
import com.github.bun133.guilib.zombies.enemy.spawn.Waver
import com.github.bun133.guilib.zombies.pop.PopHandler
import com.github.bun133.guilib.zombies.trade.TraderHandler
import net.kunmc.lab.commandlib.CommandLib
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Zombies : JavaPlugin() {
    lateinit var config: ZombiesConfig
    lateinit var ai: AIHandler
    lateinit var waver: Waver
    lateinit var spawn: SpawnHandler
    lateinit var kill: KillHandler
    lateinit var pop: PopHandler
    lateinit var trader: TraderHandler
    lateinit var core: CoreHandler
    lateinit var bossbar: BossBarHandler

    // is Game Started
    var isWaveStarted: Boolean = false

    override fun onEnable() {
        setUpCommands()

        config = ZombiesConfig(this)
        CommandLib.register(this, listOf(ZombiesCommand(this)))

        ai = AIHandler(this)
        spawn = SpawnHandler(this)
        waver = Waver(this, spawn)
        kill = KillHandler(this)
        pop = PopHandler(this)
        trader = TraderHandler(this)
        core = CoreHandler(this)
        bossbar = BossBarHandler(this)

        SpawnerPlaceListener(this)
    }

    override fun onDisable() {
    }

    private fun setUpCommands() {
        server.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doMobSpawning false")
    }

    /**
     * 現在進行中のゲームがプレイヤー側の敗北に終わった場合
     */
    fun onLose() {
        server.onlinePlayers.forEach {
            it.showTitle(Title.title(Component.text("敗北!"), Component.text("負けてしまった・・・")))
            isWaveStarted = false
        }
    }

    /**
     * 現在進行中のゲームがプレイヤー側の勝利に終わった場合
     */
    fun onWin() {
        server.onlinePlayers.forEach {
            it.showTitle(Title.title(Component.text("勝利!"), Component.text("負けてしまった・・・")))
            isWaveStarted = false
        }
    }
}