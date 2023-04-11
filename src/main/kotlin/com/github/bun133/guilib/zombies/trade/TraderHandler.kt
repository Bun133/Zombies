package com.github.bun133.guilib.zombies.trade

import com.github.bun133.guifly.gui.GUI
import com.github.bun133.guifly.gui.GUIBuilder
import com.github.bun133.guifly.gui.item.ItemBuilder
import com.github.bun133.guifly.gui.type.InventoryType
import com.github.bun133.guifly.title
import com.github.bun133.guifly.type
import com.github.bun133.guilib.zombies.Zombies
import com.github.bun133.guilib.zombies.notice
import com.github.bun133.guilib.zombies.noticeWarn
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Instrument
import org.bukkit.Note
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent

class TraderHandler(private val plugin: Zombies) : Listener {
    private val traders = mutableListOf<Trader>()

    fun register(trader: Trader) {
        traders.add(trader)
        plugin.mConfig.traderLocationList.add(trader.entity.location)
    }

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)

        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            plugin.mConfig.traderLocationList.toList().forEach {
                it.getNearbyEntitiesByType(Villager::class.java, 1.0).forEach { v ->
                    v.remove()
                }
                traders.add(Trader(plugin, it))
            }
        }, 10L)   // lateinitをごまかす
    }

    @EventHandler
    fun onClickTrader(e: PlayerInteractAtEntityEvent) {
        val trader = traders.find { it.entity == e.rightClicked }
        if (trader != null) {
            e.isCancelled = true
            plugin.server.scheduler.runTaskLater(plugin, Runnable {
                e.player.closeInventory()
                generateGUI(trader, e.player).open(e.player)
            }, 1L)
        } else {
            // Leave it as it is
        }
    }

    private fun generateGUI(trader: Trader, player: Player): GUI {
        fun handleTrading(player: Player, trading: Trading) {
            if (player.level >= trading.level) {
                // Affordable
                player.giveExpLevels(-trading.level)
                player.notice(Component.text("購入しました").color(NamedTextColor.GREEN))
                trading.onBuy(player)
            } else {
                // Cant Afford
                player.noticeWarn(Component.text("レベルが足りません！").color(NamedTextColor.RED))
                player.playNote(
                    player.location,
                    Instrument.PLING,
                    Note.natural(1, Note.Tone.E)
                )    // TODO 他の人にも聞こえるのをどうにかする
            }
        }


        val items = trader.trading.mapIndexed { index: Int, trading: Trading ->
            val x = index % 9 + 1
            val y = index / 9 + 1

            ItemBuilder(x, y).apply {
                markAsUnMovable()
                stack(trading.icon)
                click { handleTrading(player, trading) }
                shiftClick { handleTrading(player, trading) }
            }.build()
        }

        return GUIBuilder().apply {
            addItem(*items.toTypedArray())
            title(Component.text("Trade"))
            type(InventoryType.CHEST_6)
        }.build(plugin)
    }


    // 村人を無敵に
    @EventHandler
    fun onDamage(e: EntityDamageByEntityEvent) {
        if (e.damager is Player && (e.damager as Player).gameMode == GameMode.CREATIVE) return
        if (e.entity in traders.map { it.entity }) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onDamage(e: EntityDamageByBlockEvent) {
        if (e.entity in traders.map { it.entity }) {
            e.isCancelled = true
        }
    }
}