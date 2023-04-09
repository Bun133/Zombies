package com.github.bun133.guilib.zombies.enemy.ai

import com.github.bun133.guilib.zombies.Zombies
import com.github.bun133.guilib.zombies.core.Core
import com.github.bun133.guilib.zombies.core.CoreHandler
import net.kunmc.lab.configlib.value.DoubleValue
import net.minecraft.server.v1_16_R3.*
import java.util.*

class TowerAttackAI : AI<EntityCreature>(EntityCreature::class.java) {
    override fun abstractAttach(entity: EntityCreature, plugin: Zombies) {
        if (entity is EntityZombie) {
            entity.goalSelector.a(1, PathfinderGoalZombieAttack(entity, 1.0, false))
        }
        entity.goalSelector.a(
            5,
            TowardLocationAI(entity, plugin.core, plugin.config.coreBreakRange, 1.0)
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
    val core: CoreHandler,
    val satisfiedRange: DoubleValue,
    val speed: Double
) :
    PathfinderGoal() {
    init {
        a(EnumSet.of(Type.MOVE))
    }

    private var currentTarget: Core? = null
    private fun updateTarget() {
        currentTarget = core.aliveCore().minByOrNull {
            this.entity.h(it.blockLocation.x, it.blockLocation.y, it.blockLocation.z)
        }
    }

    private fun isTargetAlive(): Boolean {
        if (currentTarget != null) {
            return core.damages.getOrDefault(currentTarget, 1.0F) < 1.0F
        }
        return false
    }

    override fun a(): Boolean {
        updateTarget()
        if (currentTarget != null) {
            return this.entity.h(
                currentTarget!!.blockLocation.x,
                currentTarget!!.blockLocation.y,
                currentTarget!!.blockLocation.z
            ) > satisfiedRange.value() * satisfiedRange.value()
        }
        return false
    }

    override fun c() {
        entity.navigation.a(
            currentTarget!!.blockLocation.x,
            currentTarget!!.blockLocation.y,
            currentTarget!!.blockLocation.z,
            speed
        )
    }

    override fun b(): Boolean {
        return !entity.navigation.m() && entity.h(
            currentTarget!!.blockLocation.x,
            currentTarget!!.blockLocation.y,
            currentTarget!!.blockLocation.z
        ) > satisfiedRange.value() * satisfiedRange.value() && isTargetAlive()
    }
}