package com.github.bun133.guilib.zombies.pop

import com.github.bun133.guilib.zombies.Zombies
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

data class Popper(
    val item: ItemStack,
    val onPop: (Zombies) -> ((Location, BlockFace, Player) -> Boolean)
)

val popInstantTower = Popper(
    snowball(
        "即席タワー",
        listOf("即席でタワーを作るぞ！", "ちょっとボロボロ・・・?")
    )
) { StringSchemPopper(it, Schem(InstantTower.data, InstantTower.map))::onPop }

val pops = listOf(
    popInstantTower
)

private object InstantTower {
    val data: SchemData = listOf(
        listOf(
            "0N0",
            "W1E",
            "0S0"
        ),
        listOf(
            "0N0",
            "W1E",
            "0S0"
        )
    )

    val map = mapOf(
        '0' to BlockReflector.material(Material.AIR),
        '1' to BlockReflector.material(Material.STONE),
        'N' to BlockReflector.blockData("minecraft:ladder[facing=north]"),
        'S' to BlockReflector.blockData("minecraft:ladder[facing=south]"),
        'W' to BlockReflector.blockData("minecraft:ladder[facing=west]"),
        'E' to BlockReflector.blockData("minecraft:ladder[facing=east]"),
    )
}

private fun snowball(name: String, lores: List<String>): ItemStack {
    return ItemStack(Material.SNOWBALL).apply {
        editMeta {
            it.displayName(Component.text(name))
            it.lore(lores.map(Component::text))
        }
    }
}