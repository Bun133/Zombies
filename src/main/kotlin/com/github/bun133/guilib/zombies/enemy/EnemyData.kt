package com.github.bun133.guilib.zombies.enemy

import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType

/**
 * 敵のデータ
 */
sealed class EnemyData {
    abstract val entityType: EntityType

    // 基本形
    data class Normal(
        override val entityType: EntityType,
        val health: Double,
        val attack: Double,
        val defence: Double,
        val reward: Int
    ) : EnemyData()
}

enum class Enemy(val data: EnemyData) {
    Zombie(EnemyData.Normal(EntityType.ZOMBIE, 100.0, 1.0, 1.0, 1000));

    companion object {
        fun inferEnemy(entity: Entity): Enemy? {
            // TODO More Specific
            return values().find {
                it.data.entityType == entity.type
            }
        }
    }
}