package com.github.bun133.guilib.zombies.enemy

import com.github.bun133.guilib.zombies.Zombies
import com.github.bun133.guilib.zombies.enemy.ai.AI
import com.google.common.collect.Sets
import net.minecraft.server.v1_16_R3.EntityInsentient
import net.minecraft.server.v1_16_R3.PathfinderGoalSelector
import net.minecraft.server.v1_16_R3.PathfinderGoalWrapped
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity
import org.bukkit.entity.LivingEntity

class AIHandler(val zombies: Zombies) {
    /**
     * 敵のEntityにお手製のAIをセット
     */
    fun setAI(entity: LivingEntity, ai: AI) {
        setAI(entity as CraftLivingEntity, ai)
    }

    private fun setAI(entity: CraftLivingEntity, ai: AI) {
        entity.setAI(false)
        val e = entity.handle as EntityInsentient
        clearDefaultAI(e)
        ai.attach(e)
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