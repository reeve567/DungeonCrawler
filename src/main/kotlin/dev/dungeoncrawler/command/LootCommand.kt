package dev.dungeoncrawler.command

import dev.dungeoncrawler.loot.ExitFinder
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LootCommand : CommandExecutor {
	init {
		Bukkit.getPluginCommand("loot").executor = this
	}
	
	override fun onCommand(player: CommandSender?, p1: Command?, p2: String?, args: Array<out String>?): Boolean {
		if (player !is Player) return false
		if (args != null) {
			if (args.size == 1) {
				if (args[0].equals("compass", true))
					player.inventory.addItem(ExitFinder.itemStack)
			}
		}
		return false
	}
}