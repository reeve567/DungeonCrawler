package dev.dungeoncrawler.loot.crate

import dev.dungeoncrawler.extensions.isSimilarTo
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random
import kotlin.random.nextInt

class CrateHandler : Listener {
	@EventHandler
	fun onInteract(e: PlayerInteractEvent) {
		if (e.action == Action.RIGHT_CLICK_BLOCK) {
			if (e.clickedBlock.type == Material.CHEST || e.clickedBlock.type == Material.ENDER_CHEST) {
				Crate.values().forEach {
					if (e.clickedBlock.location == it.location) {
						// do chest stuff
						e.isCancelled = true
						if (e.item.isSimilarTo(it.getKey(), false)) {
							CrateAnimation((it.location.world.spawnEntity(it.location.clone().add(0.5, -1.0, 0.5).apply {
								yaw = when (it.location.block.data) {
									3.toByte() -> 0F // SOUTH
									2.toByte() -> 180F // NORTH
									5.toByte() -> 270F // WEST
									4.toByte() -> 90F //EAST
									else -> {
										0F
									}
								}
							}, EntityType.ARMOR_STAND) as ArmorStand).apply {
								helmet = ItemStack(it.location.block.type)
							}) {
								val k = Random.nextInt(1..100)
								var total = 0
								for (i in it.getPrizes().indices) {
									val entry = it.getPrizes()[i]
									if (i != it.getPrizes().size - 1) {
										if (k - entry.second + total <= 0) {
											// this reward
											entry.first.award(e.player)
											customName = entry.first.getLabel()
											break
										} else {
											total += entry.second
										}
									} else {
										entry.first.award(e.player)
										customName = entry.first.getLabel()
									}
								}
							}
						}
					}
				}
			}
		}
	}
}