package com.github.bun133.guilib.zombies.pop

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Directional
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

data class BlockReflector(
    val blockDataString: String?,
    val type: Material?
) {
    companion object {
        fun blockData(str: String) = BlockReflector(str, null)
        fun material(type: Material) = BlockReflector(null, type)
    }
}

data class Schem(
    val schemData: SchemData,
    // [schemData]の中のCharがそれぞれなんのBlockDataを表しているか
    val schemMap: Map<Char, BlockReflector>
)

/**
 * (Y方向に集積(X-Z平面のデータ))
 */
typealias SchemData = List<List<String>>


class StringSchemPopper(val plugin: JavaPlugin, val schem: Schem) {
    private data class ConvertedBlockReflector(
        val blockData: BlockData?,
        val type: Material?
    ) {
        fun clone() = ConvertedBlockReflector(blockData?.clone(), type)
    }

    private val convertedBlockData = schem.schemMap.mapValues {
        ConvertedBlockReflector(
            if (it.value.blockDataString != null) plugin.server.createBlockData(it.value.blockDataString!!) else null,
            it.value.type
        )
    }


    /**
     * 常に中心を[location]に合わせるように努める
     */
    fun onPop(location: Location, facing: BlockFace, player: Player) {
        val rotateTimes = rotateTimes(facing)
        // Rotate Directional
        val rotatedBlockData = convertedBlockData.mapValues { rotateRight(it.value.clone(), rotateTimes) }


        schem.schemData.forEachIndexed { index, strings ->
            val targetY = location.blockY + index
            val rotatedStrings = rotate(strings, rotateTimes)
            place(location.clone().apply { y = targetY.toDouble() }, rotatedStrings, rotatedBlockData)
        }
    }

    private fun rotateTimes(facing: BlockFace): Int {
        return when (facing) {
            BlockFace.NORTH -> 0
            BlockFace.EAST -> 1
            BlockFace.SOUTH -> 2
            BlockFace.WEST -> 3
            else -> throw IllegalArgumentException("in StringSchemPopper,facing $facing is not accepted")
        }
    }

    private fun rotate(strings: List<String>, times: Int): List<String> {
        var rotated = strings
        for (i in 0 until times) {
            rotated = rotateRight(rotated)
        }
        return rotated
    }

    /**
     * 右回りに回転させる
     * @note list内のStringのlengthにばらつきがあるときは非対応
     */
    private fun rotateRight(
        strings: List<String>,
    ): List<String> {
        val rotatedHeight = strings.maxOf { it.length }
        val rotatedWidth = strings.size

        val rotated = mutableListOf<String>()
        for (i in 0 until rotatedHeight) {
            rotated.add(strings.reversed().map { it[i] }.joinToString(""))
        }

        return rotated
    }

    private fun rotateRight(direction: BlockFace): BlockFace {
        return when (direction) {
            BlockFace.NORTH -> BlockFace.EAST
            BlockFace.EAST -> BlockFace.SOUTH
            BlockFace.SOUTH -> BlockFace.WEST
            BlockFace.WEST -> BlockFace.NORTH
            else -> throw IllegalArgumentException("BlockFace $direction is not Supported!")
        }
    }

    private fun rotateRight(r: ConvertedBlockReflector, times: Int): ConvertedBlockReflector {
        var rr = r
        for (i in 0 until times) {
            rr = rotateRight(r)
        }
        return rr
    }

    private fun rotateRight(r: ConvertedBlockReflector): ConvertedBlockReflector {
        if (r.blockData != null && r.blockData is Directional) {
            r.blockData.facing = rotateRight(r.blockData.facing)
        }
        return r
    }

    /**
     * 実際に設置する
     * @note list内のStringのlengthにばらつきがあるときは非対応
     */
    private fun place(center: Location, strings: List<String>, rotatedBlockData: Map<Char, ConvertedBlockReflector>) {
        val height = strings.size
        val width = strings[0].length
        val heightShift = height / 2
        val widthShift = width / 2

        strings.forEachIndexed { zIndex, s ->
            val shiftedZ = center.blockZ - heightShift + zIndex
            s.forEachIndexed { xIndex, c ->
                val shiftedX = center.blockX - widthShift + xIndex

                val block = center.world.getBlockAt(shiftedX, center.blockY, shiftedZ)
                val blockReflector = rotatedBlockData[c]
                if (blockReflector == null) {
                    throw IllegalArgumentException("Char $c is not listed in schemMap")
                }
                if (blockReflector.blockData != null) {
                    block.setBlockData(blockReflector.blockData, false)
                } else if (blockReflector.type != null) {
                    block.type = blockReflector.type
                } else {
                    throw IllegalArgumentException("BlockReflector doesn't have any information")
                }
            }
        }
    }
}