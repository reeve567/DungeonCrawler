package dev.dungeoncrawler.loot.crate

import dev.dungeoncrawler.DungeonCrawler
import org.bukkit.*
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework

class CrateAnimation(val armorStand: ArmorStand, block: ArmorStand.() -> Unit) {
	init {
		armorStand.setGravity(false)
		armorStand.isVisible = false
		armorStand.setBasePlate(false)

		for (i in 0..20) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(DungeonCrawler.instance, {
				if (i < 20) {
					armorStand.teleport(armorStand.location.apply {
						yaw += 18
						y += .1
						armorStand.world.playSound(armorStand.location, Sound.CLICK, .25f, 1f)
					})
				} else {
					val entity: Firework = armorStand.location.world.spawnEntity(armorStand.location.add(0.0, 1.0, 0.0), EntityType.FIREWORK) as Firework
					val meta = entity.fireworkMeta
					meta.addEffect(
							FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(Color.RED).withColor(Color.BLACK).build()
					)
					entity.fireworkMeta = meta
					Bukkit.getScheduler().scheduleSyncDelayedTask(DungeonCrawler.instance, {
						entity.playEffect(EntityEffect.FIREWORK_EXPLODE)
						Bukkit.getScheduler().scheduleSyncDelayedTask(DungeonCrawler.instance) {
							entity.remove()
						}
					}, 1)
					armorStand.isCustomNameVisible = true
					armorStand.apply(block)
					Bukkit.getScheduler().scheduleSyncDelayedTask(DungeonCrawler.instance, {
						armorStand.remove()
					}, 40)
				}
			}, i.toLong())
		}
	}
}