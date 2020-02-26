package dev.dungeoncrawler.mobs

import org.bukkit.Location
import org.bukkit.entity.EntityType

class Skeleton(loc: Location, floor: Int) : Mob(loc, "§cSkeleton", 7.0, EntityType.SKELETON, floor) {
	override fun die() {
		killArmorStand()
	}
}