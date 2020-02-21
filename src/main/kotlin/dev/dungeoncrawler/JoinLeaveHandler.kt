package dev.dungeoncrawler

import dev.dungeoncrawler.data.PlayerDataManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class JoinLeaveHandler(private val playerDataManager: PlayerDataManager) : Listener {

	@EventHandler
	fun onPlayerJoin(e: PlayerJoinEvent) {
		e.joinMessage = "§6${e.player.name} §7has joined the game."
	}

	@EventHandler
	fun onPlayerLeave(e: PlayerQuitEvent) {
		e.quitMessage = "§6${e.player.name} §7has left the game."
	}
}