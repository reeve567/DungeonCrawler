package dev.dungeoncrawler

import dev.dungeoncrawler.data.PlayerDataManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinListener(val playerDataManager: PlayerDataManager) : Listener {

	@EventHandler
	fun onPlayerJoin(e: PlayerJoinEvent) {
		e.joinMessage = "§6${e.player.name} §7has joined the game."

		playerDataManager.load(e.player.uniqueId, e.player.name)
	}
}