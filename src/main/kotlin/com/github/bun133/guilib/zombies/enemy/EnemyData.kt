package com.github.bun133.guilib.zombies.enemy

import com.github.bun133.guilib.zombies.enemy.ai.AI
import com.github.bun133.guilib.zombies.enemy.ai.TowerAttackAI
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType

/**
 * 敵のデータ
 */
sealed class EnemyData {
    abstract val entityType: EntityType

    // 敵をスポーンさせるときに考慮に入れる値
    // 強ければ強いほど大きくする
    abstract val cost: Double

    abstract val ai: AI<*>

    // 基本形
    data class Normal(
        override val entityType: EntityType,
        val health: Double,
        val attack: Double,
        val defence: Double,
        val reward: Int,
        override val ai: AI<*>
    ) : EnemyData() {
        override val cost: Double = reward.toDouble()
    }

    data class Simple(
        override val entityType: EntityType,
        val dropExp: Int,
        override val cost: Double,
        override val ai: AI<*>
    ) : EnemyData()
}

enum class Enemy(val weight: Double, val data: EnemyData) {
    Zombie(10.0, EnemyData.Simple(EntityType.ZOMBIE, 5, 1.0, TowerAttackAI())),
    Skeleton(1.0, EnemyData.Simple(EntityType.SKELETON, 10, 5.0, TowerAttackAI())),
    EnderMan(0.5, EnemyData.Simple(EntityType.ENDERMAN, 50, 10.0, TowerAttackAI())),
    IronGolem(0.1, EnemyData.Simple(EntityType.IRON_GOLEM, 1000, 100.0, TowerAttackAI()));

    companion object {
        fun inferEnemy(entity: Entity): Enemy? {
            // TODO More Specific
            return values().find {
                it.data.entityType == entity.type
            }
        }
    }
}