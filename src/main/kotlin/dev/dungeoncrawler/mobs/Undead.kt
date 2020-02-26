package dev.dungeoncrawler.mobs

import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftZombie
import org.bukkit.entity.EntityType
import org.bukkit.entity.Zombie
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Undead(loc: Location, floor: Int) : Mob(loc, "§cUndead", 10.0, EntityType.ZOMBIE, floor) {
	init {
		val zombie = entity as Zombie as CraftZombie
		zombie.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 0, false, false))
	}
	
	override fun die() {
		killArmorStand()
	}
}