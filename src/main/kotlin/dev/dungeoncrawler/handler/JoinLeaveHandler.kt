package dev.dungeoncrawler.handler

import dev.dungeoncrawler.Constants
import dev.dungeoncrawler.DungeonCrawler
import dev.dungeoncrawler.data.BankData
import dev.dungeoncrawler.data.MarketData
import dev.dungeoncrawler.data.PlayerData
import dev.dungeoncrawler.data.PlayerDataManager
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
			playerDataManager.playerData[e.player.uniqueId] = PlayerData(e.player.uniqueId, e.player.name, 0, BankData(), MarketData(), 1, 0, 1)
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(dungeonCrawler, {
			e.player.teleport(Constants.SPAWN_LOCATION)
		}, 5)

		val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
		if (scoreboard.getPlayerTeam(e.player) == null) {
			for (enum in Constants.RankTeam.values()) {
				if (e.player.hasPermission(enum.permission)) {
					scoreboard.getTeam(enum.label)?.also {
						it.addPlayer(e.player)
					}
					break
				}
			}
		}
	}

	@EventHandler
	fun onPlayerLeave(e: PlayerQuitEvent) {
		e.quitMessage = "§6${e.player.name} §7has left the game."
		playerDataManager.playerData[e.player.uniqueId]!!.party?.leave(e.player)
	}
}