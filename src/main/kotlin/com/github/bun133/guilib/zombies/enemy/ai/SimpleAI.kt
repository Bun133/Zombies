package com.github.bun133.guilib.zombies.enemy.ai

import net.minecraft.server.v1_16_R3.*
import org.bukkit.event.entity.EntityTargetEvent
import java.util.*

class SimpleAI : AI {
    override fun attach(entity: EntityCreature) {
        //TODO
        entity.targetSelector.a(0, TargetGoal(entity, 20.0) { true })
        entity.goalSelector.a(0, MoveTowardTargetGoal(entity))
    }
}

private class TargetGoal(private val entity: EntityInsentient, range: Double, filter: (EntityLiving) -> Boolean) :
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

private class MoveTowardTargetGoal(private val entity: EntityInsentient) : PathfinderGoal() {
    /**
     * このGoalが実行可能か
     */
    override fun a(): Boolean {
        val target = entity.goalTarget
        val b =
            entity.isOnGround && target != null && target is EntityHuman && target.isAlive && !target.abilities.isInvulnerable
        return b
    }

    override fun e() {
        val target = entity.goalTarget!!
        entity.a(target, 10.0F, 10.0F)
        entity.controllerMove.a(entity.locX(), entity.locY(), entity.locZ(), 1.0)
    }
}