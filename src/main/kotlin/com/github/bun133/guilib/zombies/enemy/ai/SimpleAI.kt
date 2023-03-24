package com.github.bun133.guilib.zombies.enemy.ai

import net.minecraft.server.v1_16_R3.EntityInsentient
import net.minecraft.server.v1_16_R3.EntityPlayer
import net.minecraft.server.v1_16_R3.PathfinderGoalFloat
import net.minecraft.server.v1_16_R3.PathfinderGoalNearestAttackableTarget

class SimpleAI : AI {
    override fun attach(entity: EntityInsentient) {
        //TODO
        entity.targetSelector.addGoal(0, PathfinderGoalNearestAttackableTarget(entity, EntityPlayer::class.java, false))
        entity.goalSelector.a(0, PathfinderGoalFloat(entity))
    }
}