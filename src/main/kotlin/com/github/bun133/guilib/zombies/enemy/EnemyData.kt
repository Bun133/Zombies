package com.github.bun133.guilib.zombies.enemy

import com.github.bun133.guilib.zombies.enemy.ai.AI
import com.github.bun133.guilib.zombies.enemy.ai.SimpleAI
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType

/**
 * 敵のデータ
 */
sealed class EnemyData {
    abstract val entityType: EntityType

    // 敵をスポーンさせるときに考慮に入れる値
    // 強ければ強いほど大きくする
    abstract val cost: Int

    // 基本形
    data class Normal(
        override val entityType: EntityType,
        val health: Double,
        val attack: Double,
        val defence: Double,
        val reward: Int
    ) : EnemyData() {
        override val cost: Int = reward
    }
}

enum class Enemy(val data: EnemyData, val ai: AI) {
    Zombie(EnemyData.Normal(EntityType.ZOMBIE, 10.0, 1.0, 1.0, 1), SimpleAI()),
    Skeleton(EnemyData.Normal(EntityType.SKELETON, 10.0, 2.0, 1.0, 3), SimpleAI());

    companion object {
        fun inferEnemy(entity: Entity): Enemy? {
            // TODO More Specific
            return values().find {
                it.data.entityType == entity.type
            }
        }
    }
}