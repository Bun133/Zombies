package com.github.bun133.guilib.zombies.pop

import com.github.bun133.guilib.zombies.Zombies
import com.github.bun133.guilib.zombies.notice
import com.github.bun133.guilib.zombies.noticeWarn
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class PopHandler(private val plugin: Zombies) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private data class CachedPopper(
        val item: ItemStack,
        val onPop: (Location, BlockFace, Player) -> Boolean
    )

    private val cachedPopper = pops.map { CachedPopper(it.item, it.onPop(plugin)) }

    @EventHandler
    fun onThrow(e: ProjectileHitEvent) {
        if (e.entity is Snowball) {
            val pop = cachedPopper.find { it.item.isSimilar((e.entity as Snowball).item) }
            if (pop != null) {
                if (e.hitBlock != null) {
                    // 地面に当たった場合
                    val blockFace = velocityToDirection(e.entity.velocity)
                    val player = e.entity.shooter as? Player
                    if (player != null) {
                        if (e.hitBlock!!.type != plugin.config.popSurface.value()) {
                            // 使えない範囲で使っている
                            player.noticeWarn(Component.text("ここでは使えません").color(NamedTextColor.RED))
                            player.inventory.addItem(pop.item.clone())
                            return
                        }

                        val b = pop.onPop(e.hitBlock!!.location.clone().add(0.0, 1.0, 0.0), blockFace, player)
                        if (b) {
                            player.notice(Component.text("展開しました").color(NamedTextColor.GREEN))
                        } else {
                            player.noticeWarn(Component.text("障害物に当たったため展開を中断します").color(NamedTextColor.RED))
                            player.inventory.addItem(pop.item.clone())
                        }
                    } else {
                        plugin.logger.warning("Non Player Snowball")
                    }
                } else {
                    // Hit on Entity
                    // Cancel And Return it
                    e.isCancelled = true
                    val player = e.entity.shooter as? Player
                    if (player != null) {
                        player.inventory.addItem(pop.item.clone())
                        player.noticeWarn(Component.text("展開できませんでした").color(NamedTextColor.RED))
                    } else {
                        plugin.logger.warning("Non Player Snowball")
                    }
                }
            } else {
                plugin.logger.warning("Non Pop Snowball launch")
            }
        }
    }

    private fun velocityToDirection(velocity: Vector): BlockFace {
        val x = velocity.x
        val z = velocity.z
        return if (x >= z) {
            if (x >= -z) {
                BlockFace.EAST
            } else {
                BlockFace.NORTH
            }
        } else {
            if (x >= -z) {
                BlockFace.SOUTH
            } else {
                BlockFace.WEST
            }
        }
    }
}