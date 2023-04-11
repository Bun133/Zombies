package com.github.bun133.guilib.zombies

import net.kyori.adventure.text.Component
import org.bukkit.Instrument
import org.bukkit.Location
import org.bukkit.Note
import org.bukkit.entity.Player
import org.bukkit.util.Vector

/**
 * 与えられたLocationを[range]の範囲でばらつかせます
 */
fun Location.randomize(range: Double): Location {
    return clone().add(Vector.getRandom().normalize().multiply(range))
}

/**
 * プレイヤーにチャットを送ったのちに、音を鳴らします
 */
fun Player.notice(comp: Component, ins: Instrument = Instrument.PLING, note: Note = Note.natural(1, Note.Tone.C)) {
    sendMessage(comp)
    playNote(location, ins, note)
}

fun Player.noticeWarn(comp: Component) = notice(comp,note = Note.natural(0,Note.Tone.A))