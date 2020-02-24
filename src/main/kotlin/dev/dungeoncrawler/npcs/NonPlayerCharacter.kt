package dev.dungeoncrawler.npcs

import com.mojang.authlib.GameProfile
import dev.dungeoncrawler.extensions.asCraftPlayer
import net.minecraft.server.v1_8_R3.MinecraftServer
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo
import net.minecraft.server.v1_8_R3.PlayerInteractManager
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.entity.EntityType
import java.util.*

class NonPlayerCharacter(location: Location) {
	val entity = FakePlayer(MinecraftServer.getServer(), (location.world as CraftWorld).handle, GameProfile(UUID.randomUUID(), "Xwy"), PlayerInteractManager((location.world as CraftWorld).handle))
	
	init {
		entity.setLocation(location.x, location.y, location.z, location.yaw, location.pitch)
		entity.profile.properties
		
		val info = PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entity)
		val spawn = PacketPlayOutNamedEntitySpawn(entity)
		val rem = PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entity)
		Bukkit.getOnlinePlayers().forEach {
			it.asCraftPlayer().handle.playerConnection.sendPacket(info)
			it.asCraftPlayer().handle.playerConnection.sendPacket(spawn)
			//it.asCraftPlayer().handle.playerConnection.sendPacket(rem)
		}
	}
}