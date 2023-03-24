package com.github.bun133.guilib.zombies.enemy.ai

import net.minecraft.server.v1_16_R3.EntityInsentient

interface AI {
    fun attach(entity: EntityInsentient)
}