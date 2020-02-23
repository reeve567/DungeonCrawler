package dev.dungeoncrawler.data

import net.minecraft.server.v1_8_R3.ChatComponentText
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

class PlayerDataManager {

	var playerData: HashMap<UUID, PlayerData> = HashMap()
	var marketItems = HashMap<UUID, ArrayList<Triple<Date, Pair<Double, Boolean>, ItemStack>>>()

	fun addBalance(uuid: UUID, amount: Double): Boolean {
		if (playerData.containsKey(uuid)) {
			playerData[uuid]!!.balance += amount
			val packet = PacketPlayOutChat(ChatComponentText("§6+$amount Gold"), 2)
			(Bukkit.getPlayer(uuid) as CraftPlayer).handle.playerConnection.sendPacket(packet)
			Bukkit.getPlayer(uuid).playSound(Bukkit.getPlayer(uuid).location, Sound.NOTE_PLING, .5f, 1f)
			println("Added $amount to $uuid's account for a total of ${playerData[uuid]!!.balance}")
			return true
		}
		return false
	}

	fun addBalance(player: Player, amount: Double) {
		this.addBalance(player.uniqueId, amount)
	}
}