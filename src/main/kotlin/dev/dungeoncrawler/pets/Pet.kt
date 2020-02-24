package dev.dungeoncrawler.pets

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class Pet(val owner: Player) {
	val entity: ArmorStand = Bukkit.getWorld("world").spawnEntity(owner.location, EntityType.ARMOR_STAND) as ArmorStand

	init {
		entity.setBasePlate(false)
		entity.setGravity(false)
		entity.isVisible = false

		val head = ItemStack(Material.SKULL_ITEM, 1, 3)
		val meta = head.itemMeta as SkullMeta

	}

	enum class Type {
		BEE()
	}
}