package com.github.bun133.guilib.zombies.command

import com.github.bun133.guilib.zombies.Zombies
import net.kunmc.lab.commandlib.Command
import net.kunmc.lab.commandlib.CommandContext
import net.kunmc.lab.commandlib.argument.BooleanArgument

class StartCommand(plugin: Zombies) : Command("game") {
    init {
        setDescription("Start/End Spawning and other logics")
        argument(BooleanArgument("start/end")) { toStart: Boolean, ctx: CommandContext ->
            plugin.config.isWaveStarted.value(toStart)
            if (toStart) {
                ctx.sendSuccess("ゲームを開始しました")
            } else {
                ctx.sendSuccess("ゲームを終了しました")
            }
        }
    }
}