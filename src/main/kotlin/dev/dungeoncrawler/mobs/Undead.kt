package dev.dungeoncrawler.mobs

import net.minecraft.server.v1_8_R3.GenericAttributes
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftZombie
import org.bukkit.entity.EntityType
import org.bukkit.entity.Zombie
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Undead(loc: Location, floor: Int) : Mob(loc, "§cUndead", 10.0, EntityType.ZOMBIE, floor) {
	init {
		val zombie = entity as Zombie as CraftZombie
		zombie.handle.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).value = .5
		val a = zombie.handle.javaClass.getDeclaredField("a")
		a.isAccessible = true
		zombie.handle.attributeMap.a().remove(a.get(zombie.handle))
	}
	
	override fun die() {
		killArmorStand()
	}
}