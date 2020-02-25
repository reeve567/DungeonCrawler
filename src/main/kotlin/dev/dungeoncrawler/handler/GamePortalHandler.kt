package dev.dungeoncrawler.handler

import dev.dungeoncrawler.Constants
import dev.dungeoncrawler.data.PlayerDataManager
import dev.dungeoncrawler.dungeon.Dungeon
import dev.dungeoncrawler.npcs.pets.Pet
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityTeleportEvent
import org.bukkit.event.player.PlayerTeleportEvent

class GamePortalHandler(val playerDataManager: PlayerDataManager, val dungeon: Dungeon) : Listener {
	
	@EventHandler
	fun onEnterPortal(e: PlayerTeleportEvent) {
		if (e.cause == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
			e.isCancelled = true
			val x = e.player.location.x
			val z = e.player.location.z
			
			if (x >= Constants.SpawnPortal.CornerOne.X && x <= Constants.SpawnPortal.CornerTwo.X) {
				if (z >= Constants.SpawnPortal.CornerOne.Z && z <= Constants.SpawnPortal.CornerTwo.Z) {
					// get player's level and send them to appropriate floor
					playerDataManager.playerData[e.player.uniqueId]?.also { playerData ->
						if (playerData.highestFloor == 0) {
							playerData.highestFloor = 1
						}
						if (playerData.party != null) {
							dungeon.floors[playerData.party!!.lowestFloor - 1]?.teleportPlayer(e.player)
						} else dungeon.floors[playerData.highestFloor - 1]?.teleportPlayer(e.player)
					}
				}
			}
		}
	}
	
	@EventHandler
	fun onTeleport(e: EntityTeleportEvent) {
		if (e.from.world != e.to.world)
			if (e.entity.hasMetadata("pet") || e.entity.hasMetadata("follower") || Pet.followers.containsKey(e.entity.uniqueId) || Pet.pets.containsKey(e.entity.uniqueId))
				e.isCancelled = true
	}
}