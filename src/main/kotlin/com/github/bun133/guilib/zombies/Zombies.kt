package com.github.bun133.guilib.zombies

import com.github.bun133.guilib.zombies.enemy.AIHandler
import com.github.bun133.guilib.zombies.enemy.KillHandler
import com.github.bun133.guilib.zombies.enemy.SpawnHandler
import net.kunmc.lab.commandlib.CommandLib
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class Zombies : JavaPlugin() {
    lateinit var config: ZombiesConfig
    lateinit var ai: AIHandler
    lateinit var spawn: SpawnHandler
    lateinit var kill:KillHandler
    override fun onEnable() {
        setUpCommands()

        config = ZombiesConfig(this)
        CommandLib.register(this, listOf(ZombiesCommand(this)))

        ai = AIHandler(this)
        spawn = SpawnHandler(this)
        kill = KillHandler(this)
    }

    override fun onDisable() {
    }

    private fun setUpCommands(){
        server.dispatchCommand(Bukkit.getConsoleSender(),"gamerule doMobSpawning false")
    }
}