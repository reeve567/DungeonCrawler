package dev.dungeoncrawler.mobs

import dev.dungeoncrawler.DungeonCrawler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

abstract class Mob(val loc: Location, val name: String, health: Double, type: EntityType, floor: Int) {
	var entity: LivingEntity = loc.world.spawnEntity(loc, type) as LivingEntity
	private var nameArmorStand = makeMarker()
	private var healthArmorStand = makeMarker()
	private var task = 0
	
	init {
		nameArmorStand.customName = name
		nameArmorStand.isCustomNameVisible = true
		
		entity.maxHealth = health * floor
		entity.health = entity.maxHealth
		entity.equipment.helmet = ItemStack(Material.STONE_BUTTON)
		entity.equipment.helmetDropChance = 0f
		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(DungeonCrawler.instance, {
			if (!nameArmorStand.isDead && !healthArmorStand.isDead) {
				nameArmorStand.teleport(entity.eyeLocation.add(0.0, 0.5, 0.0))
				healthArmorStand.teleport(entity.eyeLocation.add(0.0, 0.25, 0.0))
			}
		}, 3L, 3L)
		
		healthArmorStand.customName = "§4${((entity.health * 100).toInt()) / 100.0}§7/§4${entity.maxHealth}"
		healthArmorStand.isCustomNameVisible = true
	}
	
	private fun makeMarker(): ArmorStand {
		val armorStand = loc.world.spawnEntity(loc, EntityType.ARMOR_STAND) as ArmorStand
		armorStand.isMarker = true
		armorStand.isVisible = false
		armorStand.isSmall = true
		armorStand.setBasePlate(false)
		armorStand.setGravity(false)
		return armorStand
	}
	
	abstract fun die()
	
	fun onHit() {
		if (entity.health > 0)
			healthArmorStand.customName = "§4${((entity.health * 100).toInt()) / 100.0}§7/§4${entity.maxHealth}"
	}
	
	fun killArmorStand() {
		if (task != 0)
			Bukkit.getScheduler().cancelTask(task)
		nameArmorStand.remove()
		healthArmorStand.remove()
	}
}
