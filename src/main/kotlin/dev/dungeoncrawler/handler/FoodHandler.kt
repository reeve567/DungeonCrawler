package dev.dungeoncrawler.handler

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent

class FoodHandler : Listener {
	@EventHandler
	fun onFoodChange(e: FoodLevelChangeEvent) {
		e.foodLevel = 20
	}
}