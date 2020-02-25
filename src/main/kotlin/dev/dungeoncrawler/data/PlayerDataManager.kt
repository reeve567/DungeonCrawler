package dev.dungeoncrawler.data

import dev.dungeoncrawler.extensions.sendActionBar
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

class PlayerDataManager {

	var playerData: HashMap<UUID, PlayerData> = HashMap()
	var marketItems = HashMap<UUID, ArrayList<Triple<Date, Pair<Double, Boolean>, ItemStack>>>()

	fun addBalance(uuid: UUID, amount: Int): Boolean {
		if (playerData.containsKey(uuid)) {
			playerData[uuid]!!.balance += amount
			Bukkit.getPlayer(uuid).sendActionBar("§6+$amount Gold")
			Bukkit.getPlayer(uuid).playSound(Bukkit.getPlayer(uuid).location, Sound.NOTE_PLING, .5f, 1f)
			return true
		}
		return false
	}

	fun addBalance(player: Player, amount: Int) {
		this.addBalance(player.uniqueId, amount)
	}
}