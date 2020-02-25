package dev.dungeoncrawler.handler

import dev.dungeoncrawler.DungeonCrawler
import dev.dungeoncrawler.data.PlayerDataManager
import dev.dungeoncrawler.npcs.pets.Pet
import dev.dungeoncrawler.utility.Hologram
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.server.ServerListPingEvent
import java.util.*


class GeneralHandler(private val dungeonCrawler: DungeonCrawler, private val playerDataManager: PlayerDataManager) : Listener {
	@EventHandler
	fun onFoodChange(e: FoodLevelChangeEvent) {
		e.foodLevel = 20
	}
	
	@EventHandler
	fun onBreak(e: BlockBreakEvent) {
		if (e.player.gameMode != GameMode.CREATIVE) {
			e.isCancelled = true
		}
	}
	
	@EventHandler
	fun onPickup(e: PlayerPickupItemEvent) {
		if (e.item.hasMetadata("owner") && e.item.hasMetadata("gold")) {
			e.isCancelled = true
			val owner = UUID.fromString(e.item.getMetadata("owner").first().asString())
			val gold = e.item.getMetadata("gold").first().asInt()
			if (e.player.uniqueId == owner) {
				e.item.remove()
				playerDataManager.addBalance(owner, gold)
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	fun onAttack(e: EntityDamageEvent) {
		if (e.entity !is Player && e.entity !is ArmorStand) {
			if (!e.isCancelled && !(Pet.followers.containsKey(e.entity.uniqueId))) {
				val holo = Hologram("§c-${(e.finalDamage * 100).toInt() / 100.0}", e.entity.location.add(0.0, 1.5, 0.0))
				Bukkit.getScheduler().scheduleSyncDelayedTask(dungeonCrawler, {
					holo.entity.remove()
				}, 30L)
			}
		}
	}
	
	@EventHandler
	fun onPing(e: ServerListPingEvent) {
		e.motd = "§6DungeonCrawler\n§8» §cComing soon"
	}
	
	@EventHandler
	fun onChat(e: AsyncPlayerChatEvent) {
		e.format = "§8[§6${playerDataManager.playerData[e.player.uniqueId]!!.highestFloor}§8] §r${e.player.name} §7» §r${e.message}"
	}
}