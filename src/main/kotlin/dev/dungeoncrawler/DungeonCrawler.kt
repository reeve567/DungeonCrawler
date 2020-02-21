package dev.dungeoncrawler

import dev.dungeoncrawler.data.ConfigurationManager
import dev.dungeoncrawler.data.PlayerDataManager
import dev.dungeoncrawler.dungeon.Dungeon
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class DungeonCrawler : JavaPlugin() {
	val playerDataManager = PlayerDataManager()
	val configurationManager = ConfigurationManager(playerDataManager, dataFolder)
	val dungeon : Dungeon = Dungeon(this)

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
		dungeon.generate()
	}

	private fun register(vararg listener: Listener) {
		listener.forEach {
			Bukkit.getPluginManager().registerEvents(it, this)
		}
	}

	override fun onDisable() {
		configurationManager.save()
		dungeon.destroy()
	}
}