package dev.dungeoncrawler.handler

import dev.dungeoncrawler.Constants
import dev.dungeoncrawler.DungeonCrawler
import dev.dungeoncrawler.data.*
import dev.dungeoncrawler.extensions.sendScoreboard
import dev.dungeoncrawler.extensions.updateTeam
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class JoinLeaveHandler(private val dungeonCrawler: DungeonCrawler, private val playerDataManager: PlayerDataManager) : Listener {
	
	@EventHandler
	fun onPlayerJoin(e: PlayerJoinEvent) {
		e.joinMessage = "§6${e.player.name} §7has joined the game."
		if (!playerDataManager.playerData.containsKey(e.player.uniqueId)) {
			playerDataManager.playerData[e.player.uniqueId] = PlayerData(e.player.uniqueId, e.player.name, 0, BankData(), MarketData(), 1, 0, 1, PetLevelData())
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(dungeonCrawler, {
			e.player.teleport(Constants.SPAWN_LOCATION)
			e.player.sendScoreboard(playerDataManager.playerData[e.player.uniqueId]!!)
		}, 5)
		
		e.player.updateTeam()
	}
	
	@EventHandler
	fun onPlayerLeave(e: PlayerQuitEvent) {
		e.quitMessage = "§6${e.player.name} §7has left the game."
		playerDataManager.playerData[e.player.uniqueId]!!.pet?.remove()
		playerDataManager.playerData[e.player.uniqueId]!!.party?.leave(e.player)
	}
}