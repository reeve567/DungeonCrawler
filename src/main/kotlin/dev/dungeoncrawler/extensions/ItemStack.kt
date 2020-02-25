package dev.dungeoncrawler.extensions

import org.bukkit.inventory.ItemStack

fun ItemStack?.isSimilarTo(itemStack: ItemStack): Boolean {
	if (this != null)
		if (type == itemStack.type && durability == itemStack.durability && amount == itemStack.amount && hasItemMeta() == itemStack.hasItemMeta()) {
			if (itemStack.hasItemMeta()) {
				if (itemMeta.displayName == itemStack.itemMeta.displayName && itemMeta.lore == itemStack.itemMeta.lore)
					return true
			} else {
				return true
			}
		}
	return false
}
