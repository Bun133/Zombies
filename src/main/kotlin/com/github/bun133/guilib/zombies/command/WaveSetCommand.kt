package com.github.bun133.guilib.zombies.command

import com.github.bun133.guilib.zombies.Zombies
import net.kunmc.lab.commandlib.Command
import net.kunmc.lab.commandlib.CommandContext
import net.kunmc.lab.commandlib.argument.DoubleArgument
import net.kunmc.lab.commandlib.argument.IntegerArgument

class WaveSetCommand(val plugin: Zombies) : Command("setWave") {
    init {
        setDescription("現在のWaveを強制的に書き換えます")
        argument(IntegerArgument("wave")) { toUpdate: Int, commandContext: CommandContext ->
            plugin.spawn.wave = toUpdate
            commandContext.sendSuccess("ウェーブを${toUpdate}に変更しました")
        }
    }
}