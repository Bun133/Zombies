package com.github.bun133.guilib.zombies.trade

import com.github.bun133.guilib.zombies.pop.Popper
import com.github.bun133.guilib.zombies.pop.popInstantTower
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

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
val tradings = mutableListOf<Trading>(
    trade(
        ItemStack(Material.WOODEN_SWORD),
        null,
        arrayOf("木で出来た剣").textComponent(),
        0
    ),
    trade(
        ItemStack(Material.STONE_SWORD),
        null,
        arrayOf("石で出来た剣", "それなりに頼りになる").textComponent(),
        2
    ),
    trade(
        ItemStack(Material.IRON_SWORD),
        null,
        arrayOf("鉄で出来た剣", "これは頼りになる").textComponent(),
        10
    ),
    trade(
        ItemStack(Material.BOW),
        null,
        arrayOf("遠くからでも戦える").textComponent(),
        5
    ),
    trade(
        ItemStack(Material.CROSSBOW),
        null,
        arrayOf("強い。").textComponent(),
        10
    ),
    trade(
        ItemStack(Material.ARROW, 16),
        null,
        arrayOf("これがなきゃ戦えない").textComponent(),
        1
    ),
    trade(
        ItemStack(Material.ROTTEN_FLESH, 16),
        null,
        arrayOf("まずいぞ。").textComponent(),
        0
    ),
    trade(
        ItemStack(Material.COOKED_BEEF, 1),
        null,
        arrayOf("うまいぞ。").textComponent(),
        1
    ),
    trade(
        ItemStack(Material.COOKED_BEEF, 8),
        null,
        arrayOf("うまいぞ。", "まとめ買いはお得だな。賢い。").textComponent(),
        5
    ),
    *trade(
        0,
        arrayOf("ちょっと臭いな。").textComponent(),
        null,
        ItemStack(Material.LEATHER_HELMET),
        ItemStack(Material.LEATHER_CHESTPLATE),
        ItemStack(Material.LEATHER_LEGGINGS),
        ItemStack(Material.LEATHER_BOOTS),
    ).toTypedArray(),
    *trade(
        2,
        arrayOf("硬い。").textComponent(),
        null,
        ItemStack(Material.IRON_HELMET),
        ItemStack(Material.IRON_CHESTPLATE),
        ItemStack(Material.IRON_LEGGINGS),
        ItemStack(Material.IRON_BOOTS),
    ).toTypedArray(),
    *trade(
        5,
        arrayOf("ピカピカ！").textComponent(),
        null,
        ItemStack(Material.DIAMOND_HELMET),
        ItemStack(Material.DIAMOND_CHESTPLATE),
        ItemStack(Material.DIAMOND_LEGGINGS),
        ItemStack(Material.DIAMOND_BOOTS),
    ).toTypedArray(),
    *trade(
        10,
        arrayOf("カチコチ").textComponent(),
        null,
        ItemStack(Material.NETHERITE_HELMET),
        ItemStack(Material.NETHERITE_CHESTPLATE),
        ItemStack(Material.NETHERITE_LEGGINGS),
        ItemStack(Material.NETHERITE_BOOTS),
    ).toTypedArray(),
    trade(
        ItemStack(Material.POTION).apply {
            editMeta {
                it as PotionMeta
                it.addCustomEffect(PotionEffect(PotionEffectType.HEAL, 10, 1), false)
            }
        }, name = Component.text("回復のポーション"), arrayOf("万が一のために").textComponent(), 3
    ),
    trade(
        popInstantTower,
        arrayOf("きっと役に立つ").textComponent(),
        5
    )
)

private fun trade(
    level: Int,
    lores: MutableList<Component>,
    name: Component? = null,
    vararg item: ItemStack
): List<Trading> {
    return item.map {
        trade(it, name, lores, level, false)
    }
}

private fun trade(
    item: ItemStack,
    name: Component? = null,
    lores: MutableList<Component>,
    level: Int,
    extendLores: Boolean = false
): Trading {
    lores.addAll(arrayOf("", "価格:${level}レベル").textComponent())

    return Trading(item.clone().apply {
        editMeta {
            it.lore(lores)
            if (name != null) {
                it.displayName(name)
            }
        }
    }, level) {
        it.inventory.addItem(item.clone().apply {
            editMeta { i ->
                if (extendLores) i.lore(lores)
                if (name != null) {
                    i.displayName(name)
                }
            }
        })
    }
}

private fun trade(
    popper: Popper,
    lores: MutableList<Component>,
    level: Int
): Trading {
    lores.addAll(arrayOf("", "価格:${level}レベル").textComponent())

    return Trading(popper.item.clone().apply {
        editMeta {
            it.lore(lores)
        }
    }, level) {
        it.inventory.addItem(popper.item.clone())
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