package com.github.bun133.guilib.zombies.core

import com.github.bun133.guilib.zombies.Zombies
import com.github.bun133.guilib.zombies.enemy.Enemy
import com.github.bun133.guilib.zombies.enemy.animate.Animation
import net.minecraft.server.v1_16_R3.BlockPosition
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockBreakAnimation
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.Mob

data class Core(
    val blockLocation: Location
)

class CoreHandler(val zombies: Zombies) {
    init {
        zombies.server.scheduler.runTaskTimer(zombies, Runnable { update() }, 0L, 20L)
    }

    fun aliveCore() = damages.filterValues { it < 1.0F }.keys
    val damages = mutableMapOf<Core, Float>()

    init {
        zombies.mConfig.coreLocationList.value().forEach {
            damages[Core(it.toBlockLocation())] = 0.0F
        }
        zombies.mConfig.coreLocationList.onModify {
            val alreadyExist = damages.map { d -> d.key.blockLocation }
            it.map { l -> l.toBlockLocation() }.filter { l -> l !in alreadyExist }.forEach { l ->
                damages[Core(l)] = 0.0F
            }
        }
    }

    /**
     * コアのダメージをリセットします
     */
    fun resetCore() {
        damages.replaceAll { _, _ -> 0.0F }
        damages.keys.forEach {
            // ブロックの復活
            it.blockLocation.block.type = zombies.config.coreBlock.value()
        }
        loseFlag = false
    }

    private fun update() {
        updateDamage()
        updateBlockBreak()
    }

    private var loseFlag = false

    private fun updateDamage() {
        damages.filterValues { it < 1.0F }.keys.forEach { c ->
            val nearBy = c.blockLocation.getNearbyEntities(
                zombies.config.coreBreakRange.value(),
                zombies.config.coreBreakRange.value(),
                zombies.config.coreBreakRange.value()
            ).filterIsInstance<Mob>()

            animateBreak(nearBy)

            val costSum = nearBy.mapNotNull(Enemy::inferEnemy).sumOf { it.data.cost }
            val addDamage = costSum.toFloat() * zombies.config.coreBreakRate.value()

            damages[c] = damages[c]!! + addDamage

            if (damages[c]!! >= 1.0F) {
                damages[c] = 1.0F
                c.blockLocation.block.type = Material.AIR
            }
        }

        if (damages.isNotEmpty() && damages.filterValues { it < 1.0F }.isEmpty() && !loseFlag) {
            zombies.onLose()
            loseFlag = true
        }
    }

    private fun animateBreak(entities: List<Mob>) {
        entities.forEach {
            Animation.SwingHand.animate(it)
        }
    }

    private fun updateBlockBreak() {
        damages.forEach { (core, damage) ->
            sendBlockDamage(core.blockLocation, damage)
        }
    }

    // Bukkitのバグに強引に対処
    private fun sendBlockDamage(location: Location, damage: Float) {
        if (damage == 0.0F) {
            zombies.server.onlinePlayers.forEach {
                it as CraftPlayer
                val packet = PacketPlayOutBlockBreakAnimation(
                    it.handle.id,
                    BlockPosition(location.blockX, location.blockY, location.blockZ),
                    -1
                )
                it.handle.playerConnection!!.sendPacket(packet)
            }
        } else {
            zombies.server.onlinePlayers.forEach {
                it.sendBlockDamage(location, damage)
            }
        }
    }
}