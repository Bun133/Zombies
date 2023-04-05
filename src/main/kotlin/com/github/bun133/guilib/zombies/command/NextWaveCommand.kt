package com.github.bun133.guilib.zombies.command

import com.github.bun133.guilib.zombies.Zombies
import net.kunmc.lab.commandlib.Command

class NextWaveCommand(val plugin: Zombies) : Command("nextWave") {
    init {
        setDescription("強制的に次のWaveに進みます")
        execute {
            if (!plugin.isWaveStarted) {
                it.sendFailure("ウェーブが開始されていません")
            } else {
                plugin.waver.nextWave()
                it.sendSuccess("次のウェーブに進みます")
            }
        }
    }
}