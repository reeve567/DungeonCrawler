package dev.dungeoncrawler.dungeon

import dev.dungeoncrawler.Constants
import dev.dungeoncrawler.DungeonCrawler
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.collections.HashSet

class Party(val leader: Player) {
	var invited = HashSet<Player>()
	var members = HashSet<Player>()
	var lowestFloor = 1

	init {
		val playerDataManager = DungeonCrawler.instance.playerDataManager
		lowestFloor = playerDataManager.playerData[leader.uniqueId]!!.highestFloor
	}
	
	fun leave(player: Player) {
		if (player.isOnline)
			player.teleport(Constants.SPAWN_LOCATION)
		members.remove(player)
		partyBroadcast("§6${player.name} has left the party.")
		val playerDataManager = DungeonCrawler.instance.playerDataManager

		var temp = playerDataManager.playerData[leader.uniqueId]!!.highestFloor
		for (member in members) {
			val playerData = playerDataManager.playerData[member.uniqueId]!!
			if (playerData.highestFloor < temp) {
				temp = playerData.highestFloor
			}
		}
		if (temp < lowestFloor) {
			lowestFloor = temp
			partyBroadcast("§6Party floor has been shifted to ${lowestFloor}.")
		}
	}

	fun sendInvite(player: Player) {
		if (invited.size + members.size < 5) {
			if (invited.contains(player)) {
				leader.sendMessage("§cYou have already invited this player.")
			} else {
				player.sendMessage("§6You have been invited to ${leader.name}'s party.")
				invited.add(player)
				Bukkit.getScheduler().scheduleSyncDelayedTask(DungeonCrawler.instance, {
					if (invited.contains(player)) {
						player.sendMessage("§6Invite to ${leader.name}'s party has expired.")
						leader.sendMessage("§6Invite to ${player.name} has expired.")
						invited.remove(player)
					}
				}, 1200)
			}
		} else {
			leader.sendMessage("§6Adding this player would make your party too large.")
		}
	}

	fun acceptInvite(player: Player) {
		val playerData = DungeonCrawler.instance.playerDataManager.playerData[player.uniqueId]!!
		if (playerData.party != null) {
			player.sendMessage("§cPlease leave your current party first.")
		} else {
			invited.remove(player)
			playerData.party = this
			player.teleport(Constants.SPAWN_LOCATION)
			members.add(player)
			partyBroadcast("§6${player.name} has joined the party.")

			if (playerData.highestFloor < lowestFloor) {
				lowestFloor = playerData.highestFloor
				partyBroadcast("§6Party floor has been shifted to ${lowestFloor}.")
			}
		}
	}

	fun partyBroadcast(string: String) {
		leader.sendMessage(string)
		members.forEach { it.sendMessage(string) }
	}

}