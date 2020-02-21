package dev.dungeoncrawler

import dev.dungeoncrawler.data.PlayerDataManager
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class DungeonCrawler : JavaPlugin() {
	val playerDataManager = PlayerDataManager()

	init {
		instance = this
	}

	companion object {
		lateinit var instance: DungeonCrawler
	}

	override fun onEnable() {
		register(
				JoinLeaveHandler(playerDataManager),
				BankHandler(playerDataManager)
		)

		Bukkit.getOnlinePlayers().forEach {
			playerDataManager.load(it.uniqueId, it.name)
		}
	}

	private fun register(vararg listener: Listener) {
		listener.forEach {
			Bukkit.getPluginManager().registerEvents(it, this)
		}
	}

	override fun onDisable() {
		Bukkit.getOnlinePlayers().forEach {
			playerDataManager.saveAndRemove(it.uniqueId)
		}
	}
}