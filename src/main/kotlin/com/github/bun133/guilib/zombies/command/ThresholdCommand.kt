package com.github.bun133.guilib.zombies.command

import com.github.bun133.guilib.zombies.Zombies
import net.kunmc.lab.commandlib.Command
import net.kunmc.lab.commandlib.CommandContext
import net.kunmc.lab.commandlib.argument.IntegerArgument

class ThresholdCommand(val plugin: Zombies) : Command("threshold") {
    init {
        setDescription("現在のThresholdを強制的に書き換えます")
        argument(IntegerArgument("threshold")) { toUpdate: Int, commandContext: CommandContext ->
            plugin.spawn.forceUpdateBottomThreshold(toUpdate)
            commandContext.sendSuccess("値を変更しました")
        }
    }
}