package dev.dungeoncrawler.command

import dev.dungeoncrawler.loot.crate.Crate
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class KeyCommand : CommandExecutor {
	init {
		Bukkit.getPluginCommand("key").executor = this
	}
	
	override fun onCommand(player: CommandSender?, p1: Command?, p2: String?, args: Array<out String>?): Boolean {
		if (player == null || player !is Player) {
			return false
		}
		if (args != null) {
			if (args.size == 1) {
				Crate.values().forEach { if (it.name.equals(args[0], true)) player.inventory.addItem(it.getKey()) }
			}
		}
		return true
	}
}