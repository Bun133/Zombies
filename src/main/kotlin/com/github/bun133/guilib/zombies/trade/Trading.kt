package com.github.bun133.guilib.zombies.trade

import net.kyori.adventure.text.Component
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

/**
 * 村人の取引一覧
 */
val tradings = mutableListOf(
    trade(
        ItemStack(Material.WOODEN_SWORD),
        Component.text("木の剣"),
        arrayOf("木で出来た剣", "ちょっともろいらしい", "", "安くしておくよ。").textComponent(),
        0
    ),
    trade(
        ItemStack(Material.STONE_SWORD),
        Component.text("石の剣"),
        arrayOf("石で出来た剣", "それなりに頼りになる").textComponent(),
        2
    ),
    trade(
        ItemStack(Material.IRON_SWORD),
        Component.text("鉄の剣"),
        arrayOf("鉄で出来た剣", "これは頼りになる").textComponent(),
        10
    ),
    trade(
        ItemStack(Material.COOKED_BEEF, 1),
        Component.text("肉"),
        arrayOf("うまいぞ。").textComponent(),
        1
    ),
    trade(
        ItemStack(Material.COOKED_BEEF, 8),
        Component.text("肉"),
        arrayOf("うまいぞ。", "まとめ買いはお得だな。賢い。").textComponent(),
        5
    )
)

private fun trade(
    item: ItemStack,
    name: Component,
    lores: MutableList<Component>,
    level: Int,
    extendLores: Boolean = false
): Trading {
    lores.addAll(arrayOf("", "価格:${level}レベル").textComponent())

    return Trading(item.clone().apply {
        editMeta {
            it.lore(lores)
            it.displayName(name)
        }
    }, level) {
        it.inventory.addItem(item.clone().apply {
            editMeta { i ->
                if (extendLores) i.lore(lores)
                i.displayName(name)
            }
        })
    }
}

private fun item(
    material: Material,
    amount: Int = 1,
    displayName: Component? = null,
    lores: List<Component>? = null
): ItemStack {
    val item = ItemStack(material, amount)
    item.editMeta {
        it.displayName(displayName)
        if (lores != null) {
            it.lore(lores)
        }
    }

    return item
}

private fun Array<String>.textComponent(): MutableList<Component> = this.map { Component.text(it) }.toMutableList()