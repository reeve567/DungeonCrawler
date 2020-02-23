package dev.dungeoncrawler.extensions

import org.bukkit.Material
import org.bukkit.block.Block

fun Block.isSafe(): Boolean {
	return this.type.isSolid && this.getRelative(0, 1, 0).type == Material.AIR && this.getRelative(0, 2, 0).type == Material.AIR
}