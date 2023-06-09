package com.github.bun133.guilib.zombies.enemy.ai

import com.github.bun133.guilib.zombies.Zombies
import net.minecraft.server.v1_16_R3.EntityInsentient

abstract class AI<E : EntityInsentient>(private val entityClazz: Class<E>) {
    protected val attached = mutableListOf<E>()
    protected abstract fun abstractAttach(entity: E, plugin: Zombies)
    fun attach(entity: E, plugin: Zombies) {
        attached.add(entity)
        abstractAttach(entity, plugin)
    }

    fun canAssign(entity: EntityInsentient): Boolean {
        return entityClazz.isInstance(entity)
    }

    fun forceAttach(entity: EntityInsentient, zombies: Zombies) {
        assert(canAssign(entity))
        attach(entity as E, zombies)
    }
}