package dev.dungeoncrawler

import dev.dungeoncrawler.command.BalanceCommand
import dev.dungeoncrawler.command.PartyCommand
import dev.dungeoncrawler.command.SpawnCommand
import dev.dungeoncrawler.data.ConfigurationManager
import dev.dungeoncrawler.data.PlayerDataManager
import dev.dungeoncrawler.dungeon.Dungeon
import dev.dungeoncrawler.command.PetCommand
import dev.dungeoncrawler.dungeon.SetCommand
import dev.dungeoncrawler.extensions.sendHeaderAndFooter
import dev.dungeoncrawler.extensions.updateTeam
import dev.dungeoncrawler.handler.BankHandler
import dev.dungeoncrawler.handler.GamePortalHandler
import dev.dungeoncrawler.handler.GeneralHandler
import dev.dungeoncrawler.handler.JoinLeaveHandler
import org.bukkit.Bukkit
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Zombie
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class DungeonCrawler : JavaPlugin() {
	val playerDataManager = PlayerDataManager()
	private val configurationManager = ConfigurationManager(playerDataManager, dataFolder)
	 var dungeon: Dungeon? = null
	
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
				JoinLeaveHandler(this, playerDataManager),
				BankHandler(playerDataManager),
				GamePortalHandler(playerDataManager, dungeon!!),
				GeneralHandler(this, playerDataManager)
		)
		loadCommands()
		loadTeams()
		sendTab()
		
		//NonPlayerCharacter(Location(world, 10000.5, 41.0, 10015.5, 0f, 0f))
	}
	
	private fun register(vararg listener: Listener) {
		listener.forEach {
			Bukkit.getPluginManager().registerEvents(it, this)
		}
	}
	
	override fun onDisable() {
		for (player in Bukkit.getOnlinePlayers()) {
			player.teleport(Constants.SPAWN_LOCATION)
		}
		
		Bukkit.getWorld("world").getEntitiesByClass(Zombie::class.java).forEach {
			if (it.hasMetadata("follower")) {
				it.remove()
			}
		}
		Bukkit.getWorld("world").getEntitiesByClass(ArmorStand::class.java).forEach {
			if (it.hasMetadata("pet")) {
				it.remove()
			}
		}
		
		configurationManager.save()
		dungeon?.destroy()
	}
	
	private fun loadCommands() {
		SpawnCommand(this)
		BalanceCommand(playerDataManager)
		PartyCommand(playerDataManager)
		SetCommand(playerDataManager)
		PetCommand(playerDataManager)
	}
	
	private fun sendTab() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, {
			Bukkit.getOnlinePlayers().forEach {
				it.sendHeaderAndFooter("§8§m«---[§r §6§lDungeonCrawler §r§8§m]---»\n§cdungeoncrawler.dev", "§6Players Online: ${Bukkit.getOnlinePlayers().size}")
			}
		}, 20L, 20L)
	}
	
	private fun loadTeams() {
		val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
		for (enum in Constants.RankTeam.values()) {
			if (scoreboard.getTeam(enum.label) == null) {
				val tem = scoreboard.registerNewTeam(enum.label)
				tem.prefix = enum.prefix
			} else {
				scoreboard.getTeam(enum.label).unregister()
				val tem = scoreboard.registerNewTeam(enum.label)
				tem.prefix = enum.prefix
			}
		}
		
		for (player in Bukkit.getOnlinePlayers()) {
			player.updateTeam()
		}
	}
}