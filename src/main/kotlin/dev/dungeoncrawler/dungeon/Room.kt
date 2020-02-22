package dev.dungeoncrawler.dungeon

import dev.dungeoncrawler.Constants
import net.minecraft.server.v1_8_R3.ChunkSection
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk


class Room(private val floor: Floor, val x: Int, val z: Int, private val pfbIndex: Int) {
	fun create(delay: Int) {
		val pfbX = (pfbIndex / Constants.PREFAB_SIZE)
		val pfbZ = (pfbIndex % Constants.PREFAB_SIZE)
		val cx = (1000 + pfbX) * 16
		val cz = (1000 + pfbZ) * 16
//        for(y in 0 until 16) {
//            for (x in 0 until 16) {
//                for (z in 0 until 16) {
//                    var b : Block = floor.world.getBlockAt(cx + x, 10 + y, cz + z)
//                    var r : Block = floor.world.getBlockAt(this.x * 16 + x, 10 + y, this.z * 16 + z)
//                    r.type = b.type
//                    r.setData(b.data, true)
//                }
//            }
//        }
		val prefab: Chunk = floor.world.getChunkAt(Location(floor.world, cx.toDouble(), 10.0, cz.toDouble()))
		val destination: Chunk = floor.world.getChunkAt(Location(floor.world, x * 16.0, 10.0, z * 16.0))

		copyChunk(prefab, destination, delay)
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

	fun copyChunk(from: Chunk, to: Chunk, delay: Int) {
		val fromCC = from as CraftChunk
		val toCC = to as CraftChunk
		val fromChunk: net.minecraft.server.v1_8_R3.Chunk = fromCC.handle
		val toChunk: net.minecraft.server.v1_8_R3.Chunk = toCC.handle

		val tsec: Array<ChunkSection> = fromChunk.sections.clone()
		val asec = arrayOfNulls<ChunkSection>(tsec.size)

		for (n in tsec.indices) {
			if (tsec[n] != null) {
				asec[n] = ChunkSection(tsec[n].yPosition,true,tsec[n].idArray.clone())
				asec[n]!!.a(tsec[n].emittedLightArray)
			} else {
				asec[n] = tsec[n]
			}
		}
		toChunk.a(asec)
		toChunk.removeEntities()
		floor.world.refreshChunk(toChunk.bukkitChunk.x, toChunk.bukkitChunk.z)

		/*fun run() {
			for (x in 0 until 16) {
				for (y in 10 until 26) {
					for (z in 0 until 16) {
						val fromBlock = from.getBlock(x, y, z)
						val toBlock = to.getBlock(x, y, z)
						toBlock.type = fromBlock.type
						toBlock.state.data = MaterialData(toBlock.state.type, toBlock.state.rawData)
						toBlock.state.update(true)
					}
				}
			}
		}

		if (delay > 0) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(DungeonCrawler.instance, {
				run()
			}, delay.toLong())
		} else run()*/


	}

	fun destroy() {
		val destination: Chunk = floor.world.getChunkAt(Location(floor.world, x * 16.0, 10.0, z * 16.0))
		val empty: Chunk = floor.world.getChunkAt(Location(floor.world, 999 * 16.0, 999 * 16.0, 999 * 16.0))
		copyChunk(empty, destination, 0)
	}
}