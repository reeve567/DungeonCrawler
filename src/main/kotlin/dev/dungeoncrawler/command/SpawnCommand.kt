package dev.dungeoncrawler.command

import dev.dungeoncrawler.Constants
import dev.dungeoncrawler.DungeonCrawler
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SpawnCommand(val dungeonCrawler: DungeonCrawler) : CommandExecutor {
	init {
		Bukkit.getPluginCommand("spawn").executor = this
	}

	override fun onCommand(player: CommandSender?, p1: Command?, p2: String?, p3: Array<out String>?): Boolean {
		if (player is Player) {
			val originalPosition = player.location.block.location
			fun hasntMoved(): Boolean {
				return originalPosition == player.location.block.location
			}

			player.sendMessage("§6Teleporting to spawn in 5 seconds, please don't move...")
			Bukkit.getScheduler().scheduleSyncDelayedTask(dungeonCrawler, {
				if (hasntMoved())
					Bukkit.getScheduler().scheduleSyncDelayedTask(dungeonCrawler, {
						if (hasntMoved())
							Bukkit.getScheduler().scheduleSyncDelayedTask(dungeonCrawler, {
								if (hasntMoved())
									Bukkit.getScheduler().scheduleSyncDelayedTask(dungeonCrawler, {
										if (hasntMoved())
											Bukkit.getScheduler().scheduleSyncDelayedTask(dungeonCrawler, {
												player.teleport(Constants.SPAWN_LOCATION)
											}, 20)
										else
											player.sendMessage("§cTeleportation cancelled.")
									}, 20)
								else
									player.sendMessage("§cTeleportation cancelled.")
							}, 20)
						else
							player.sendMessage("§cTeleportation cancelled.")
					}, 20)
				else
					player.sendMessage("§cTeleportation cancelled.")
			}, 20)

		}
		return true
	}
}