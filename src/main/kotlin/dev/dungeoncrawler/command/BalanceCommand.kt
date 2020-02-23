package dev.dungeoncrawler.command

import dev.dungeoncrawler.data.PlayerDataManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BalanceCommand(private val playerDataManager: PlayerDataManager) : CommandExecutor {
	init {
		Bukkit.getPluginCommand("balance").executor = this
	}

	override fun onCommand(player: CommandSender?, p1: Command?, p2: String?, p3: Array<out String>?): Boolean {
		if (player is Player) {
			playerDataManager.playerData[player.uniqueId]?.also { playerData ->
				player.sendMessage("§6You have a balance of ${playerData.balance} gold.")
			}
		}
		return true
	}
}