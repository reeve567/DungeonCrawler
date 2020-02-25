package dev.dungeoncrawler.loot

import dev.dungeoncrawler.DungeonCrawler
import dev.dungeoncrawler.extensions.isSimilarTo
import dev.dungeoncrawler.utility.item
import dev.dungeoncrawler.utility.itemMeta
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class ExitFinder : Listener {
	companion object {
		val itemStack = item(Material.COMPASS) {
			itemMeta {
				displayName = "§6Exit Finder §7(Right Click)"
				lore = listOf("§7Use this to get to the next floor much faster!", "§7You should probably calibrate it though...")
			}
		}
		
		fun setTarget(player: Player) {
			val playerData = DungeonCrawler.instance.playerDataManager.playerData[player.uniqueId]!!
			DungeonCrawler.instance.dungeon!!.floors.get(playerData.highestFloor - 1)?.also { floor ->
				var closest: Location? = null
				var distance = Double.MAX_VALUE
				floor.rooms.forEach { room ->
					if (room.isCheckpoint) {
						val roomLoc = room.getChunk().getBlock(0, player.location.blockY, 0).location
						if (closest == null || distance > roomLoc.distance(player.location)) {
							closest = roomLoc
							distance = roomLoc.distance(player.location)
						}
					}
				}
				if (closest != null) {
					player.sendMessage("§6Room found.")
					player.compassTarget = closest
				}
			}
		}
	}
	
	@EventHandler
	fun onClick(e: PlayerInteractEvent) {
		if (e.action == Action.RIGHT_CLICK_AIR) {
			if (e.item.isSimilarTo(itemStack)) {
				setTarget(e.player)
			}
		}
	}
}