package dev.dungeoncrawler

import dev.dungeoncrawler.data.PlayerDataManager
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class BankHandler(private val playerDataManager: PlayerDataManager) : Listener {
	@EventHandler
	fun onBankOpen(e: PlayerInteractEvent) {
		if (e.action == Action.RIGHT_CLICK_BLOCK) {
			if (e.clickedBlock.type == Material.ENDER_CHEST) {
				e.isCancelled = true
				playerDataManager.playerData[e.player.uniqueId]?.also {
					it.bankData.pages[1]?.openPage(e.player)
				}
				e.player.playSound(e.clickedBlock.location, Sound.CHEST_OPEN, .5f, .35f)
			}
		}
	}
}