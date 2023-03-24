package com.github.bun133.guilib.zombies

import org.bukkit.Location
import org.bukkit.util.Vector

/**
 * 与えられたLocationを[range]の範囲でばらつかせます
 */
fun Location.randomize(range: Double): Location {
    return clone().add(Vector.getRandom().normalize().multiply(range))
}