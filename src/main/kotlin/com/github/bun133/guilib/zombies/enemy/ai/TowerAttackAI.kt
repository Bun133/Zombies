package com.github.bun133.guilib.zombies.enemy.ai

import com.github.bun133.guilib.zombies.Zombies
import net.kunmc.lab.configlib.value.DoubleValue
import net.kunmc.lab.configlib.value.collection.LocationSetValue
import net.minecraft.server.v1_16_R3.*
import org.bukkit.Location
import java.util.*

class TowerAttackAI : AI<EntityCreature>(EntityCreature::class.java) {
    override fun abstractAttach(entity: EntityCreature, plugin: Zombies) {
        if (entity is EntityZombie) {
            entity.goalSelector.a(1, PathfinderGoalZombieAttack(entity, 1.0, false))
        }
        entity.goalSelector.a(
            5,
            TowardLocationAI(entity, plugin.config.coreLocationList, plugin.config.coreBreakRange, 1.0)
        )

        if (entity is EntityZombie) {
            entity.targetSelector.a(0, PathfinderGoalNearestAttackableTarget(entity, EntityHuman::class.java, true))
        } else {
            entity.targetSelector.a(0, PathfinderGoalNearestAttackableTarget(entity, EntityHuman::class.java, false))
        }
    }
}

class TowardLocationAI(
    val entity: EntityInsentient,
    targetValue: LocationSetValue,
    val satisfiedRange: DoubleValue,
    val speed: Double
) :
    PathfinderGoal() {
    init {
        a(EnumSet.of(Type.MOVE))
        targetValue.onModify {
            targetLocation = it.toList()
            this.c()
        }
    }

    private var targetLocation: List<Location> = targetValue.value().toList()
    private var currentTarget: Location? = null
    private fun nearestTarget(): Location? {
        return targetLocation.minByOrNull {
            this.entity.h(it.x, it.y, it.z)
        }
    }

    override fun a(): Boolean {
        val nearest = nearestTarget()
        if (nearest != null) {
            return this.entity.h(nearest.x, nearest.y, nearest.z) > satisfiedRange.value() * satisfiedRange.value()
        }
        return false
    }

    override fun c() {
        currentTarget = nearestTarget()!!
        entity.navigation.a(currentTarget!!.x, currentTarget!!.y, currentTarget!!.z, speed)
    }

    override fun b(): Boolean {
        return !entity.navigation.m() && entity.h(
            currentTarget!!.x,
            currentTarget!!.y,
            currentTarget!!.z
        ) > satisfiedRange.value() * satisfiedRange.value()
    }
}