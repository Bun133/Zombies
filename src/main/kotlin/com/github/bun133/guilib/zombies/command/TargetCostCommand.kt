package com.github.bun133.guilib.zombies.command

import com.github.bun133.guilib.zombies.Zombies
import net.kunmc.lab.commandlib.Command
import net.kunmc.lab.commandlib.CommandContext
import net.kunmc.lab.commandlib.argument.DoubleArgument

class TargetCostCommand(val plugin: Zombies) : Command("targetCost") {
    init {
        setDescription("現在のTargetCostを強制的に書き換えます")
        argument(DoubleArgument("cost")) { toUpdate: Double, commandContext: CommandContext ->
            plugin.spawn.targetSpawnCost = toUpdate
            commandContext.sendSuccess("値を変更しました")
        }
    }
}