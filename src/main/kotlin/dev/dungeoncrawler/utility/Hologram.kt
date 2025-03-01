package dev.dungeoncrawler.utility

import dev.dungeoncrawler.DungeonCrawler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.metadata.FixedMetadataValue

class Hologram(text: String, location: Location) {
	val entity = Bukkit.getWorld("world").spawnEntity(location, EntityType.ARMOR_STAND) as ArmorStand
	
	init {
		entity.isSmall = true
		entity.isVisible = false
		entity.setBasePlate(false)
		entity.setGravity(false)
		entity.customName = text
		entity.isCustomNameVisible = true
		entity.isMarker = true
		entity.setMetadata("hologram", FixedMetadataValue(DungeonCrawler.instance, true))
	}
}