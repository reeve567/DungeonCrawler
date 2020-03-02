package dev.dungeoncrawler.handler

import dev.dungeoncrawler.DungeonCrawler
import dev.dungeoncrawler.data.PlayerDataManager
import org.bukkit.GameMode
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.server.ServerListPingEvent
import java.util.*


class GeneralHandler(private val dungeonCrawler: DungeonCrawler, private val playerDataManager: PlayerDataManager) : Listener {
	
	@EventHandler
	fun onBreak(e: BlockBreakEvent) {
		if (e.player.gameMode != GameMode.CREATIVE) {
			e.isCancelled = true
		}
	}
	
	@EventHandler
	fun onPickup(e: PlayerPickupItemEvent) {
		if (e.item.hasMetadata("owner")) {
			if (e.item.hasMetadata("gold")) {
				e.isCancelled = true
				val owner = UUID.fromString(e.item.getMetadata("owner").first().asString())
				val gold = e.item.getMetadata("gold").first().asInt()
				if (e.player.uniqueId == owner) {
					e.item.remove()
					playerDataManager.addBalance(owner, gold)
				}
			}
		}
	}
	
	@EventHandler
	fun onInteract(e: EntityDamageEvent) {
		if (e.entity.hasMetadata("hologram") || e.entity is ArmorStand)
			e.isCancelled = true
	}
	
	@EventHandler
	fun onPing(e: ServerListPingEvent) {
		e.motd = "§6DungeonCrawler\n§8» §cComing soon"
	}
	
	@EventHandler
	fun onChat(e: AsyncPlayerChatEvent) {
		e.format = "§8[§6${playerDataManager.playerData[e.player.uniqueId]!!.highestFloor}§8] §r${DungeonCrawler.chat.getGroupPrefix("world", DungeonCrawler.chat.getPrimaryGroup(e.player))}${e.player.name} §7» §r${e.message}".replace('&','§')
	}
}