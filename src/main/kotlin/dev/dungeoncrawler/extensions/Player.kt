package dev.dungeoncrawler.extensions

import dev.dungeoncrawler.DungeonCrawler
import dev.dungeoncrawler.utility.item
import dev.dungeoncrawler.utility.itemMeta
import net.minecraft.server.v1_8_R3.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import kotlin.math.min

fun Player.sendActionBar(string: String) {
	val packet = PacketPlayOutChat(ChatComponentText(string), 2)
	this.asCraftPlayer().handle.playerConnection.sendPacket(packet)
}

fun Player.dropGold(amount: Int, location: Location) {
	val entity = location.world.dropItemNaturally(location, item(Material.GOLD_INGOT) {
		this.amount = min(64, amount)
		itemMeta {
			displayName = "$amount §6Gold"
		}
	})
	entity.customName = entity.itemStack.itemMeta.displayName
	entity.isCustomNameVisible = true
	entity.setMetadata("gold", FixedMetadataValue(DungeonCrawler.instance, amount))
	entity.setMetadata("owner", FixedMetadataValue(DungeonCrawler.instance, uniqueId.toString()))
	
	val packet = PacketPlayOutEntityDestroy(entity.entityId)
	Bukkit.getOnlinePlayers().forEach { player ->
		if (player.uniqueId != uniqueId) {
			player.asCraftPlayer().handle.playerConnection.sendPacket(packet)
		}
	}
}

fun Player.asCraftPlayer(): CraftPlayer = this as CraftPlayer