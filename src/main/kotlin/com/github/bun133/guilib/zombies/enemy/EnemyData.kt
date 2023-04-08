package com.github.bun133.guilib.zombies.enemy

import com.github.bun133.guilib.zombies.enemy.ai.AI
import com.github.bun133.guilib.zombies.enemy.ai.TowerAttackAI
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.persistence.PersistentDataType

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
        override val ai: AI<*>,
        // 手に持ってるアイテムをクリアするフラッグ
        val itemClear: Boolean = true
    ) : EnemyData()
}

enum class Enemy(val weight: Double, val data: EnemyData) {
    Zombie(10.0, EnemyData.Simple(EntityType.ZOMBIE, 5, 1.0, TowerAttackAI())),
    Skeleton(1.0, EnemyData.Simple(EntityType.SKELETON, 10, 5.0, TowerAttackAI(), itemClear = false)),
    EnderMan(0.5, EnemyData.Simple(EntityType.ENDERMAN, 50, 10.0, TowerAttackAI())),
    IronGolem(0.1, EnemyData.Simple(EntityType.IRON_GOLEM, 1000, 100.0, TowerAttackAI()));

    companion object {
        private val key = NamespacedKey("zombies", "entitymarker")

        // Entityにマーキングします
        fun markDataTag(enemy: Enemy, entity: Entity) {
            entity.persistentDataContainer.set(key, PersistentDataType.STRING, enemy.name)
        }

        private fun getDataTag(entity: Entity): String? {
            return entity.persistentDataContainer.get(key, PersistentDataType.STRING)
        }

        fun inferEnemy(entity: Entity): Enemy? {
            val tag = getDataTag(entity)
            val match = values().filter {
                it.data.entityType == entity.type &&
                        tag == it.name
            }

            if (match.size == 1) return match[0]
            return null
        }
    }
}