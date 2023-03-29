package com.github.bun133.guilib.zombies.enemy

import com.github.bun133.guilib.zombies.Zombies
import com.github.bun133.guilib.zombies.enemy.ai.AI
import com.google.common.collect.Sets
import net.minecraft.server.v1_16_R3.EntityInsentient
import net.minecraft.server.v1_16_R3.PathfinderGoalSelector
import net.minecraft.server.v1_16_R3.PathfinderGoalWrapped

class AIHandler(val zombies: Zombies) {
    /**
     * Safely Attach AI to type-unknown entity,
     * @return true - correctly attached,false - failed to attach.
     */
    fun safeSetAI(entity: EntityInsentient, ai: AI<*>): Boolean {
        return if (ai.canAssign(entity)) {
            clearDefaultAI(entity)
            zombies.logger.info("Attaching AI Safely")
            ai.forceAttach(entity)
            true
        } else {
            false
        }
    }

    /**
     * 敵のEntityにお手製のAIをセット
     */
    fun <E : EntityInsentient> setAI(entity: E, ai: AI<E>) {
        clearDefaultAI(entity)
        zombies.logger.info("Attaching AI")
        ai.attach(entity)
    }

    private fun clearDefaultAI(e: EntityInsentient) {
        PathfinderGoalSelector::class.java.getDeclaredField("d")
            .apply { isAccessible = true }
            .set(e.goalSelector, Sets.newLinkedHashSet<PathfinderGoalWrapped>())

        PathfinderGoalSelector::class.java.getDeclaredField("d")
            .apply { isAccessible = true }
            .set(e.targetSelector, Sets.newLinkedHashSet<PathfinderGoalWrapped>())
    }
}