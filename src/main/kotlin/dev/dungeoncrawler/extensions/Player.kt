package dev.dungeoncrawler.extensions

import net.minecraft.server.v1_8_R3.*
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

fun Player.sendActionBar(string: String) {
	val packet = PacketPlayOutChat(ChatComponentText(string), 2)
	this.asCraftPlayer().handle.playerConnection.sendPacket(packet)
}

fun Player.asCraftPlayer(): CraftPlayer = this as CraftPlayer