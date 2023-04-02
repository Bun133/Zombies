package com.github.bun133.guilib.zombies.enemy.ai

import com.github.bun133.guilib.zombies.Zombies
import net.kunmc.lab.configlib.value.LocationValue
import net.minecraft.server.v1_16_R3.*
import org.bukkit.Location
import java.util.*

class TowerAttackAI : AI<EntityCreature>(EntityCreature::class.java) {
    override fun attach(entity: EntityCreature, plugin: Zombies) {
        if (entity is EntityZombie) {
            entity.goalSelector.a(1, PathfinderGoalZombieAttack(entity, 1.0, false))
        }
        entity.goalSelector.a(5, TowardLocationAI(entity, plugin.config.targetLocation, 2.5, 1.0))

        if (entity is EntityZombie) {
            entity.targetSelector.a(0, PathfinderGoalNearestAttackableTarget(entity, EntityHuman::class.java, true))
        }else{
            entity.targetSelector.a(0, PathfinderGoalNearestAttackableTarget(entity, EntityHuman::class.java, false))
        }
    }
}

class TowardLocationAI(
    val entity: EntityInsentient,
    targetValue: LocationValue,
    val satisfiedRange: Double,
    val speed: Double
) :
    PathfinderGoal() {
    init {
        a(EnumSet.of(Type.MOVE))
        targetValue.onModify {
            targetLocation = it
            this.c()
        }
    }

    private var targetLocation: Location = targetValue.value()

    override fun a(): Boolean {
        return this.entity.h(targetLocation.x, targetLocation.y, targetLocation.z) > satisfiedRange * satisfiedRange
    }

    override fun c() {
        val b = entity.navigation.a(targetLocation.x, targetLocation.y, targetLocation.z, speed)
        if (b) {
            println("Failed to Navigate")
        }
    }

    override fun b(): Boolean {
        val b = !entity.navigation.m() && this.entity.h(
            targetLocation.x,
            targetLocation.y,
            targetLocation.z
        ) > satisfiedRange * satisfiedRange
        if (b) println("Satisfied!")
        return b
    }
}