package dev.dungeoncrawler.command

import dev.dungeoncrawler.Constants
import dev.dungeoncrawler.data.PlayerData
import dev.dungeoncrawler.data.PlayerDataManager
import dev.dungeoncrawler.dungeon.Party
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PartyCommand(private val playerDataManager: PlayerDataManager) : CommandExecutor {
	init {
		Bukkit.getPluginCommand("party").executor = this
	}
	
	override fun onCommand(player: CommandSender?, p1: Command?, p2: String?, args: Array<out String>?): Boolean {
		if (player !is Player)
			return false
		val playerData = playerDataManager.playerData[player.uniqueId]!!
		
		if (args != null) {
			if (args.isEmpty()) {
				sendHelp(player)
			} else if (args[0].equals("invite", true)) {
				if (args.size >= 2) {
					val target = Bukkit.getPlayer(args[1])
					if (target != null && target.uniqueId != player.uniqueId) {
						createPartyIfNecessary(player, playerData)
						if (playerData.party!!.leader.uniqueId != player.uniqueId) {
							player.sendMessage("§cYou must be the party leader to invite someone.")
						} else playerData.party!!.sendInvite(target)
					} else player.sendMessage("§cPlayer not found.")
				} else player.sendMessage("§cPlease include a player to invite.")
			} else if (args[0].equals("kick", true)) {
				if (args.size >= 2) {
					val target = Bukkit.getPlayer(args[1])
					if (target != null && target.uniqueId != player.uniqueId) {
						createPartyIfNecessary(player, playerData)
						if (playerData.party!!.leader.uniqueId != player.uniqueId) {
							player.sendMessage("§cYou must be the party leader to invite someone.")
						} else {
							playerData.party!!.leave(target)
							target.sendMessage("§6You have been kicked from the party.")
						}
					} else player.sendMessage("§cPlayer not found.")
				} else player.sendMessage("§cPlease include a player to kick.")
			} else if (args[0].equals("leave", true)) {
				if (playerData.party != null) {
					if (playerData.party!!.leader.uniqueId == player.uniqueId) {
						player.sendMessage("§6Disbanding the party...")
						playerData.party!!.members.forEach {
							it.teleport(Constants.SPAWN_LOCATION)
							playerDataManager.playerData[it.uniqueId]!!.party = null
							it.sendMessage("§6The party has been disbanded.")
						}
						playerData.party!!.invited.forEach {
							it.sendMessage("§6A party you were invited to was disbanded.")
						}
						playerData.party!!.invited = HashSet()
						playerData.party!!.members = HashSet()
						playerData.party = null
					} else playerData.party!!.leave(player)
				} else player.sendMessage("§cYou are not in a party.")
			} else if (args[0].equals("join", true)) {
				if (args.size >= 2) {
					val target = Bukkit.getPlayer(args[1])
					if (target != null && target.uniqueId != player.uniqueId) {
						val party = playerDataManager.playerData[target.uniqueId]!!.party
						if (party != null) {
							if (party.invited.contains(player)) {
								party.acceptInvite(player)
							} else player.sendMessage("§cYou were not invited to this party.")
						} else player.sendMessage("§cParty does not exist.")
					} else player.sendMessage("§cPlayer not found.")
				} else player.sendMessage("§cPlease include a player to join.")
			} else if (args[0].equals("list", true)) {
				val party = playerData.party
				if (party != null) {
					player.sendMessage("§6Party:")
					player.sendMessage("§7Leader: ${party.leader.name}")
					player.sendMessage("§7Members:")
					party.members.forEach { player.sendMessage("§6${it.name}") }
				} else player.sendMessage("§cYou are not in a party.")
			}
		} else {
			sendHelp(player)
		}
		return true
	}
	
	private fun sendHelp(player: Player) {
		player.sendMessage(listOf(
				"§6Party Help",
				"§7/§6party join <user>",
				"§7/§6party invite <user>",
				"§7/§6party kick <user>",
				"§7/§6party leave",
				"§7/§6party list"
		).toTypedArray())
	}
	
	private fun createPartyIfNecessary(player: Player, playerData: PlayerData) {
		if (playerData.party == null) {
			playerData.party = Party(player)
			player.sendMessage("§6Party created.")
		}
	}
	
}