package dev.dungeoncrawler.npcs

import com.mojang.authlib.GameProfile
import net.minecraft.server.v1_8_R3.EntityPlayer
import net.minecraft.server.v1_8_R3.MinecraftServer
import net.minecraft.server.v1_8_R3.PlayerInteractManager
import net.minecraft.server.v1_8_R3.WorldServer

class FakePlayer(minecraftserver: MinecraftServer, worldserver: WorldServer, gameprofile: GameProfile, playerinteractmanager: PlayerInteractManager) : EntityPlayer(minecraftserver, worldserver, gameprofile, playerinteractmanager) {
}