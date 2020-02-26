package dev.dungeoncrawler.extensions

import org.bukkit.inventory.Inventory

fun Inventory.hasFreeSpace(): Boolean {
	return this.contents.filterNotNull().isNotEmpty()
}