package dev.dungeoncrawler.command

import dev.dungeoncrawler.data.PlayerDataManager
import dev.dungeoncrawler.pets.Pet
import dev.dungeoncrawler.utility.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PetCommand(val playerDataManager: PlayerDataManager) : CommandExecutor {
	init {
		Bukkit.getPluginCommand("pet").executor = this
	}
	
	override fun onCommand(player: CommandSender?, p1: Command?, p2: String?, p3: Array<out String>?): Boolean {
		if (player !is Player)
			return false
		val playerData = playerDataManager.playerData[player.uniqueId]!!
		if (playerData.pet == null || (playerData.pet != null && !playerData.pet!!.summoned)) {
			gui(9) {
				for (i in Pet.Type.values().indices) {
					val type = Pet.Type.values()[i]
					var level = 1
					var exp = 0
					if (playerData.petLevels.levels.containsKey(type.name)) {
						level = playerData.petLevels.levels[type.name]!!.first
						exp = playerData.petLevels.levels[type.name]!!.second
					}
					
					
					clickableItem {
						if (player.hasPermission("dungeoncrawler.pets.${type.name.toLowerCase()}")) {
							clickAction {
								isCancelled = true
								playerData.pet = Pet(player, type, playerData)
								println("what")
								player.closeInventory()
							}
							
							itemStack = item(type.getHead()) {
								itemMeta {
									displayName = "§6${type.petName}"
									lore = listOf("§7Damage: §6${type.getDamageTotal(level)}", "§7Attack speed: §6${((type.attackSpeed / 20.0) * 100).toInt() / 100.0}", "§7Description:", "§6${type.description}", "§7Level: $level", "§7EXP: $exp/${Pet.getExpToLevel(level)}")
								}
							}
						} else {
							itemStack = item(Material.STAINED_GLASS_PANE) {
								durability = 15
								itemMeta {
									displayName = "§c${type.petName}"
									lore = listOf("§cLocked", "§7Damage: §6${type.baseDamage}", "§7Attack speed: §6${((type.attackSpeed / 20.0) * 100).toInt() / 100.0}", "§7Description:", "§6${type.description}")
								}
							}
							clickAction { isCancelled = true }
						}
						slot = i
					}
				}
			}.open(player)
		} else {
			player.sendMessage("§6You sent back your pet.")
			playerData.pet!!.saveLevels()
			playerData.pet!!.summoned = false
			playerData.pet = null
		}
		
		return true
	}
}