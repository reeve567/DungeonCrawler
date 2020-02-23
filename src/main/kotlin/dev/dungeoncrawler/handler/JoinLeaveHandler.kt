package dev.dungeoncrawler.handler

import dev.dungeoncrawler.data.BankData
import dev.dungeoncrawler.data.MarketData
import dev.dungeoncrawler.data.PlayerData
import dev.dungeoncrawler.data.PlayerDataManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class JoinLeaveHandler(private val playerDataManager: PlayerDataManager) : Listener {

	@EventHandler
	fun onPlayerJoin(e: PlayerJoinEvent) {
		e.joinMessage = "§6${e.player.name} §7has joined the game."
		if (!playerDataManager.playerData.containsKey(e.player.uniqueId)) {
			playerDataManager.playerData[e.player.uniqueId] = PlayerData(e.player.uniqueId, e.player.name, 0, BankData(), MarketData(), 1, 0, 1)
		}

	}

	@EventHandler
	fun onPlayerLeave(e: PlayerQuitEvent) {
		e.quitMessage = "§6${e.player.name} §7has left the game."
	}
}