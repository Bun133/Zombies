package com.github.bun133.guilib.zombies.enemy.ai

import net.minecraft.server.v1_16_R3.EntityCreature
import net.minecraft.server.v1_16_R3.EnumHand
import net.minecraft.server.v1_16_R3.PathEntity
import net.minecraft.server.v1_16_R3.PathfinderGoal
import java.util.*

class AttackAI : AI<EntityCreature>(EntityCreature::class.java) {
    override fun attach(entity: EntityCreature) {
        entity.targetSelector.a(0, TargetGoal(entity, 20.0) { true })

        entity.goalSelector.a(0, AttackTargetGoal(entity, 1.0, 20))
        entity.goalSelector.a(1, MoveTowardTargetGoal(entity, 1.0))
    }
}

class AttackTargetGoal(
    private val entity: EntityCreature,
    private val attackRange: Double,
    private val attackCool: Int
) : PathfinderGoal() {
    init {
        a(EnumSet.of(Type.TARGET))
    }

    private var path: PathEntity? = null
    private var lastAttacked: Long = 0L

    /**
     * イニシエーター
     * ターゲットまでの道のりが分かったらこのGoalを処理する
     */
    override fun a(): Boolean {
        val target = entity.goalTarget ?: return false
//        if (!target.isAlive || target.isInvulnerable) return false
//        path = entity.navigation.a(target, 0)
//        return path != null
        val distance = this.entity.h(target.locX(), target.locY(), target.locZ())
        return entity.world.time - lastAttacked >= attackCool && distance < attackRange
    }

    override fun c() {
        println("Attack Start")
        // Pathに沿って進むように設定
//        entity.navigation.a(path!!, speed)
        // おこだぞ
//        entity.isAggressive = true
        super.c()
    }


    override fun e() {
        val target = entity.goalTarget!!
        // 見つめる
        entity.controllerLook.a(target, 10.0F, 10.0F)

        // ターゲットまでの距離
        entity.swingHand(EnumHand.MAIN_HAND)
        entity.attackEntity(target)
    }

    override fun b(): Boolean {
        return false
//        val target = entity.goalTarget ?: return false
//        return target.isAlive && !target.isInvulnerable
    }

    override fun d() {
        super.d()
        println("Attack END")
    }
}