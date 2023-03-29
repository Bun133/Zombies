package com.github.bun133.guilib.zombies.enemy.ai

import net.minecraft.server.v1_16_R3.EntityInsentient

abstract class AI<E : EntityInsentient>(private val entityClazz: Class<E>) {
    abstract fun attach(entity: E)
    fun canAssign(entity: EntityInsentient): Boolean {
        return entityClazz.isInstance(entity)
    }

    fun forceAttach(entity: EntityInsentient) {
        assert(canAssign(entity))
        attach(entity as E)
    }
}