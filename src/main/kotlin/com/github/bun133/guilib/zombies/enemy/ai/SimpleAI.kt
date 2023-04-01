package com.github.bun133.guilib.zombies.enemy.ai

import net.minecraft.server.v1_16_R3.*
import org.bukkit.event.entity.EntityTargetEvent
import java.util.*

class SimpleAI : AI<EntityCreature>(EntityCreature::class.java) {
    override fun attach(entity: EntityCreature) {
        //TODO
        entity.targetSelector.a(0, TargetGoal(entity, 20.0) { true })
        entity.goalSelector.a(0, MoveTowardTargetGoal(entity))
    }
}

class TargetGoal(private val entity: EntityInsentient, range: Double, filter: (EntityLiving) -> Boolean) :
    PathfinderGoalTarget(entity, true) {
    private var target: EntityHuman? = null
    private val condition = PathfinderTargetCondition().apply {
//        a(entity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE)!!.value)
        a(range)
        a(filter)
    }

    init {
        a(EnumSet.of(Type.TARGET))
    }

    /**
     * [c]を回していいかどうかのコンディション・事前準備関数
     */
    override fun a(): Boolean {
        target = entity.world.a(condition, entity, entity.locX(), entity.headY, entity.locZ())
        return target != null
    }

    /**
     * 実際の処理(TargetをSetするだけ)
     */
    override fun c() {
        entity.setGoalTarget(target!!, EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true)
        super.c()
    }
}

class MoveTowardTargetGoal(private val entity: EntityInsentient, private val speed: Double = 1.0) : PathfinderGoal() {
    init {
        a(EnumSet.of(Type.MOVE))
    }

    private var path: PathEntity? = null

    /**
     * このGoalが実行可能か
     */
    override fun a(): Boolean {
        val target = entity.goalTarget ?: return false
        path = entity.navigation.a(target, 0)
        val b = path != null && target.isAlive && !target.isInvulnerable
        println("Move ${if(b)"will start" else "will not start"}")
        return b
    }

    override fun c() {
        println("Move Start")
        // Pathに沿って進むように設定
        entity.navigation.a(path!!, speed)
        // おこだぞ
        entity.isAggressive = true
    }

    override fun e() {
        val target = entity.goalTarget!!
        // 見つめる
        entity.a(target, 10.0F, 10.0F)
//        entity.controllerMove.a(target.locX(), target.locY(), target.locZ(), speed)
    }

    override fun d() {
        println("Move End")
        entity.navigation.o()
    }
}