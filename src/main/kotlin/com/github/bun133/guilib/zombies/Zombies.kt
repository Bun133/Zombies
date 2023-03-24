package com.github.bun133.guilib.zombies

import com.github.bun133.guilib.zombies.command.ZombiesCommand
import com.github.bun133.guilib.zombies.enemy.AIHandler
import com.github.bun133.guilib.zombies.enemy.KillHandler
import com.github.bun133.guilib.zombies.enemy.SpawnHandler
import com.github.bun133.guilib.zombies.trade.TraderHandler
import net.kunmc.lab.commandlib.CommandLib
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Zombies : JavaPlugin() {
    lateinit var config: ZombiesConfig
    lateinit var ai: AIHandler
    lateinit var spawn: SpawnHandler
    lateinit var kill: KillHandler
    lateinit var trader: TraderHandler

    override fun onEnable() {
        setUpCommands()

        config = ZombiesConfig(this)
        CommandLib.register(this, listOf(ZombiesCommand(this)))

        ai = AIHandler(this)
        spawn = SpawnHandler(this)
        kill = KillHandler(this)
        trader = TraderHandler(this)
    }

    override fun onDisable() {
    }

    private fun setUpCommands() {
        server.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doMobSpawning false")
    }
}