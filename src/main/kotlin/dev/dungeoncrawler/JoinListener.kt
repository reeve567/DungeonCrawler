package dev.dungeoncrawler

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinListener : Listener {

	@EventHandler
	fun onPlayerJoin(e: PlayerJoinEvent) {
		e.joinMessage = "§6${e.player.name} §7has joined the game."
	}
}