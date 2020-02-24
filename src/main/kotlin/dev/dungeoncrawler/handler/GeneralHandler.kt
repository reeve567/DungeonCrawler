package dev.dungeoncrawler.handler

import dev.dungeoncrawler.data.PlayerDataManager
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import java.util.*

class GeneralHandler(private val playerDataManager: PlayerDataManager) : Listener {
	@EventHandler
	fun onFoodChange(e: FoodLevelChangeEvent) {
		e.foodLevel = 20
	}
	
	@EventHandler
	fun onBreak(e: BlockBreakEvent) {
		if (e.player.gameMode != GameMode.CREATIVE) {
			e.isCancelled = true
		}
	}
	
	@EventHandler
	fun onPickup(e: PlayerPickupItemEvent) {
		if (e.item.hasMetadata("owner") && e.item.hasMetadata("gold")) {
			e.isCancelled = true
			val owner = UUID.fromString(e.item.getMetadata("owner").first().asString())
			val gold = e.item.getMetadata("gold").first().asInt()
			if (e.player.uniqueId == owner) {
				e.item.remove()
				playerDataManager.addBalance(owner, gold)
			}
		}
	}
}