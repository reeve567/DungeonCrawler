package dev.dungeoncrawler.handler

import dev.dungeoncrawler.Constants
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class GamePortalHandler : Listener {

	@EventHandler
	fun onEnterPortal(e: PlayerTeleportEvent) {
		if (e.cause == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
			e.isCancelled = true
			val x = e.player.location.x
			val z = e.player.location.z

			if (x >= Constants.SpawnPortal.CornerOne.X && x <= Constants.SpawnPortal.CornerTwo.X) {
				if (z >= Constants.SpawnPortal.CornerOne.Z && z <= Constants.SpawnPortal.CornerTwo.Z) {
					// get player's level and send them to appropriate floor
				}
			}
		}
	}
}