package dev.dungeoncrawler.handler

import dev.dungeoncrawler.data.PlayerDataManager
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class MarketHandler(private val playerDataManager: PlayerDataManager) : Listener {
	@EventHandler
	fun onMarketOpen(e: PlayerInteractEvent) {
		if (e.action == Action.RIGHT_CLICK_BLOCK) {
			if (e.clickedBlock.type == Material.TRAPPED_CHEST) {
				e.isCancelled = true


				e.player.playSound(e.clickedBlock.location, Sound.CHEST_OPEN, .5f, .5f)
			}
		}
	}
}