package dev.dungeoncrawler.mobs

import net.minecraft.server.v1_8_R3.*
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSilverfish
import org.bukkit.entity.EntityType
import org.bukkit.entity.Silverfish

class Mite(loc: Location, floor: Int) : Mob(loc, "§cMite", 4.0, EntityType.SILVERFISH, floor) {
	init {
		val silver : EntitySilverfish = ((entity as Silverfish) as CraftSilverfish).handle
		val goalSelector = PathfinderGoalSelector((loc.world as CraftWorld).handle.methodProfiler)
		goalSelector.a(1, PathfinderGoalFloat(silver))
		goalSelector.a(4, PathfinderGoalMeleeAttack(silver, EntityHuman::class.java, 1.0, false))
		
		silver.goalSelector = goalSelector
	}
	override fun die() {
		killArmorStand()
	}
}