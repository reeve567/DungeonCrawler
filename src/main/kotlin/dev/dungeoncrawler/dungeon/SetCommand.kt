package dev.dungeoncrawler.dungeon

import dev.dungeoncrawler.data.PlayerDataManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetCommand(private val playerDataManager: PlayerDataManager) : CommandExecutor {
	init {
		Bukkit.getPluginCommand("set").executor = this
	}
	
	override fun onCommand(player: CommandSender?, p1: Command?, p2: String?, args: Array<out String>?): Boolean {
		if (player !is Player) {
			return false
		}
		val playerData = playerDataManager.playerData[player.uniqueId]!!
		
		if (args != null && args.isNotEmpty()) {
			if (args[0].equals("floor", true))
				playerData.highestFloor = args[1].toInt()
		}
		return true
	}
}