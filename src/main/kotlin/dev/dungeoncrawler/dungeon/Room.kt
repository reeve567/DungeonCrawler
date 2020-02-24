package dev.dungeoncrawler.dungeon

import dev.dungeoncrawler.Constants
import net.minecraft.server.v1_8_R3.ChunkSection
import org.bukkit.*
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk
import org.bukkit.entity.Player

class Room(private val floor: Floor, val x: Int, val z: Int, private val pfbIndex: Int, val isCheckpoint: Boolean = false) {
	fun create() {
		val prefab: Chunk = if (isCheckpoint) {
			println("checkpoint")
			floor.world.getChunkAt(998, 998)
		} else {
			val pfbX = (pfbIndex / Constants.PREFAB_SIZE)
			val pfbZ = (pfbIndex % Constants.PREFAB_SIZE)
			val cx = (1000 + pfbX) * 16
			val cz = (1000 + pfbZ) * 16
			
			floor.world.getChunkAt(Location(floor.world, cx.toDouble(), 10.0, cz.toDouble()))
		}
		val destination: Chunk = floor.world.getChunkAt(Location(floor.world, x * 16.0, 10.0, z * 16.0))
		
		copyChunk(prefab, destination)
	}
	
	fun createDoors() {
		fun doZ(diffX: Int) {
			val startZ = (z * 16) + 6
			var startX = (x * 16) + diffX
			for (z2 in startZ..startZ + 3) {
				for (y2 in 11..12) {
					floor.world.getBlockAt(startX, y2, z2).type = Material.AIR
				}
			}
			floor.world.getBlockAt(startX, 13, startZ + 1).type = Material.AIR
			floor.world.getBlockAt(startX, 13, startZ + 2).type = Material.AIR
			
		}
		
		fun doX(diffZ: Int) {
			val startZ = (z * 16) + diffZ
			val startX = (x * 16) + 6
			for (x2 in startX..startX + 3) {
				for (y2 in 11..12) {
					floor.world.getBlockAt(x2, y2, startZ).type = Material.AIR
				}
			}
			floor.world.getBlockAt(startX + 1, 13, startZ).type = Material.AIR
			floor.world.getBlockAt(startX + 2, 13, startZ).type = Material.AIR
		}
		
		
		doZ(-1)
		doZ(16)
		doX(-1)
		doX(16)
	}
	
	fun createFakeDoors(player: Player, type: Material = Material.STAINED_GLASS, data: Byte = 14) {
		fun doZ(diffX: Int) {
			val startZ = (z * 16) + 6
			val startX = (x * 16) + diffX
			for (z2 in startZ..startZ + 3) {
				for (y2 in 11..12) {
					val location = Location(Bukkit.getWorld("world"), startX.toDouble(), y2.toDouble(), z2.toDouble())
					if (location.block.type != Material.AIR)
						return
					player.sendBlockChange(location, type, data)
				}
			}
			player.sendBlockChange(Location(Bukkit.getWorld("world"), startX.toDouble(), 13.0, startZ + 1.0), type, data)
			player.sendBlockChange(Location(Bukkit.getWorld("world"), startX.toDouble(), 13.0, startZ + 2.0), type, data)
		}
		
		fun doX(diffZ: Int) {
			val startZ = (z * 16) + diffZ
			val startX = (x * 16) + 6
			for (x2 in startX..startX + 3) {
				for (y2 in 11..12) {
					val location = Location(Bukkit.getWorld("world"), x2.toDouble(), y2.toDouble(), startZ.toDouble())
					if (location.block.type != Material.AIR)
						return
					player.sendBlockChange(location, type, data)
				}
			}
			player.sendBlockChange(Location(Bukkit.getWorld("world"), startX + 1.0, 13.0, startZ.toDouble()), type, data)
			player.sendBlockChange(Location(Bukkit.getWorld("world"), startX + 2.0, 13.0, startZ.toDouble()), type, data)
		}
		
		doZ(-1)
		doZ(16)
		doX(-1)
		doX(16)
		if (type == Material.STAINED_GLASS)
			player.playSound(player.location, Sound.DOOR_CLOSE, 0.5f, 1f)
		else
			player.playSound(player.location, Sound.DOOR_OPEN, 0.5f, 1f)
	}
	
	
	fun copyChunk(from: Chunk, to: Chunk) {
		val fromCC = from as CraftChunk
		val toCC = to as CraftChunk
		val fromChunk: net.minecraft.server.v1_8_R3.Chunk = fromCC.handle
		val toChunk: net.minecraft.server.v1_8_R3.Chunk = toCC.handle
		
		val tsec: Array<ChunkSection?> = fromChunk.sections.clone()
		val asec = arrayOfNulls<ChunkSection>(tsec.size)
		
		for (n in tsec.indices) {
			if (tsec[n] != null) {
				asec[n] = ChunkSection(tsec[n]!!.yPosition, true, tsec[n]!!.idArray.clone())
				asec[n]!!.a(tsec[n]!!.emittedLightArray)
			} else {
				asec[n] = tsec[n]
			}
		}
		toChunk.a(asec)
		toChunk.removeEntities()
		floor.world.refreshChunk(toChunk.bukkitChunk.x, toChunk.bukkitChunk.z)
		
	}
	
	fun destroy() {
		val destination: Chunk = floor.world.getChunkAt(Location(floor.world, x * 16.0, 10.0, z * 16.0))
		val empty: Chunk = floor.world.getChunkAt(Location(floor.world, 999 * 16.0, 999 * 16.0, 999 * 16.0))
		copyChunk(empty, destination)
	}
	
	fun getChunk(): Chunk {
		return Bukkit.getWorld("world").getChunkAt(x, z)
	}
}