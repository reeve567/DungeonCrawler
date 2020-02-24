package dev.dungeoncrawler.extensions

import dev.dungeoncrawler.Constants
import dev.dungeoncrawler.DungeonCrawler
import dev.dungeoncrawler.utility.TextConverter
import dev.dungeoncrawler.utility.item
import dev.dungeoncrawler.utility.itemMeta
import net.md_5.bungee.api.chat.TextComponent
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

fun Player.updateTeam() {
	val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
	if (scoreboard.getPlayerTeam(this) == null) {
		for (enum in Constants.RankTeam.values()) {
			if (hasPermission(enum.permission)) {
				scoreboard.getTeam(enum.label)?.also {
					it.addPlayer(this)
				}
				break
			}
		}
	}
}

fun Player.sendHeaderAndFooter(header: String, footer: String) {
	val componentHeader = IChatBaseComponent.ChatSerializer.a(TextConverter.convert(header))
	val componentFooter = IChatBaseComponent.ChatSerializer.a(TextConverter.convert(footer))
	val packet = PacketPlayOutPlayerListHeaderFooter(componentHeader)
	
	val b = packet.javaClass.getDeclaredField("b")
	b.isAccessible = true
	b.set(packet, componentFooter)
	
	player.asCraftPlayer().handle.playerConnection.sendPacket(packet)
}

fun Player.asCraftPlayer(): CraftPlayer = this as CraftPlayer