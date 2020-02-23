package dev.dungeoncrawler

import dev.dungeoncrawler.command.SpawnCommand
import dev.dungeoncrawler.data.ConfigurationManager
import dev.dungeoncrawler.data.PlayerDataManager
import dev.dungeoncrawler.dungeon.Dungeon
import dev.dungeoncrawler.handler.*
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class DungeonCrawler : JavaPlugin() {
	private val playerDataManager = PlayerDataManager()
	private val configurationManager = ConfigurationManager(playerDataManager, dataFolder)
	private var dungeon: Dungeon? = null

	init {
		instance = this
	}

	companion object {
		lateinit var instance: DungeonCrawler
	}

	override fun onEnable() {
		val world = Bukkit.getWorld("world")
		world.setGameRuleValue("doMobSpawning", "false")
		world.setGameRuleValue("keepInventory", "true")
		world.setSpawnLocation(10000, 41, 10000)

		dungeon = Dungeon(this, playerDataManager)
		dungeon!!.generate()

		register(
				JoinLeaveHandler(playerDataManager),
				BankHandler(playerDataManager),
				GamePortalHandler(playerDataManager, dungeon!!),
				FoodHandler()
		)

		loadCommands()
	}

	private fun register(vararg listener: Listener) {
		listener.forEach {
			Bukkit.getPluginManager().registerEvents(it, this)
		}
	}

	override fun onDisable() {
		configurationManager.save()
		dungeon?.destroy()
	}

	fun loadCommands() {
		SpawnCommand(this)
	}
}