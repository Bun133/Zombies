package com.github.bun133.guilib.zombies.enemy.animate

import net.minecraft.server.v1_16_R3.EntityInsentient
import net.minecraft.server.v1_16_R3.EnumHand
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMob
import org.bukkit.entity.Mob

sealed class Animation {
    abstract fun animate(entity: EntityInsentient)
    fun animate(entity: Mob) {
        animate((entity as CraftMob).handle)
    }

    object SwingHand : Animation() {
        override fun animate(entity: EntityInsentient) {
            entity.swingHand(EnumHand.MAIN_HAND)
        }
    }
}
