package dev.dungeoncrawler.loot.crate

import dev.dungeoncrawler.DungeonCrawler
import dev.dungeoncrawler.extensions.dropLocalItem
import dev.dungeoncrawler.extensions.hasFreeSpace
import dev.dungeoncrawler.npcs.pets.PetType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Prize<T>(private val value: T, val name: String = "") {
	fun award(player: Player) {
		if (value is Int) {
			DungeonCrawler.instance.playerDataManager.addBalance(player, value)
		} else if (value is PetType) {
			if (!player.hasPermission(value.getPermission()))
				DungeonCrawler.permission.playerAdd(player, value.getPermission())
		} else if (value is String) {
			if (!player.hasPermission(value))
				DungeonCrawler.permission.playerAdd(player, value)
		} else if (value is ItemStack) {
			if (player.inventory.hasFreeSpace()) {
				player.inventory.addItem(value)
			} else {
				player.dropLocalItem(value, player.location)
			}
		} else {
			error("wrong type of Prize -> $name")
		}
		player.sendMessage("§6You won ${getLabel()}")
	}
	
	fun getLabel(): String {
		if (name != "") {
			return name
		} else {
			if (value is Int) {
				return "§r$value §6Gold"
			} else if (value is ItemStack) {
				return value.itemMeta.displayName
			} else if (value is PetType) {
				return value.petName
			} else {
				return "null"
			}
		}
	}
}