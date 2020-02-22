package dev.dungeoncrawler

import dev.dungeoncrawler.command.SpawnCommand
import dev.dungeoncrawler.data.ConfigurationManager
import dev.dungeoncrawler.data.PlayerDataManager
import dev.dungeoncrawler.dungeon.Dungeon
import dev.dungeoncrawler.handler.BankHandler
import dev.dungeoncrawler.handler.GamePortalHandler
import dev.dungeoncrawler.handler.JoinLeaveHandler
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
		register(
				JoinLeaveHandler(playerDataManager),
				BankHandler(playerDataManager),
				GamePortalHandler()
		)
		val world = Bukkit.getWorld("world")
		world.setGameRuleValue("doMobSpawning", "false")
		world.setSpawnLocation(10000, 41, 10000)

		dungeon = Dungeon(this)
		dungeon!!.generate()
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
		SpawnCommand()
	}
}