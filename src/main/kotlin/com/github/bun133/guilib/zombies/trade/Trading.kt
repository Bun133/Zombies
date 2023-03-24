package com.github.bun133.guilib.zombies.trade

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * 取引
 */
data class Trading(
    // 取引画面で表示するアイコン代わりのItemStack
    val icon: ItemStack,

    // 価格(レベル)
    val level: Int,

    // 購入時処理(レベルの差し引き後)
    val onBuy: (Player) -> Unit
)

val tradings = mutableListOf(
    Trading(ItemStack(Material.GRAY_WOOL), 1) { it.inventory.addItem(ItemStack(Material.GRAY_WOOL)) }
)