package dev.dungeoncrawler.dungeon

import dev.dungeoncrawler.Constants
import net.minecraft.server.v1_8_R3.ChunkSection
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk


class Room(private val floor : Floor, val x: Int, val z: Int, private val pfbIndex: Int) {
    fun create() {
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
        val destination: Chunk = floor.world.getChunkAt(Location(floor.world,x * 16.0, 10.0, z * 16.0))
        copyChunk(prefab, destination)
    }

    fun copyChunk(from: Chunk, to: Chunk) {
        val fromCC = from as CraftChunk
        val toCC = to as CraftChunk
        val fromChunk: net.minecraft.server.v1_8_R3.Chunk = fromCC.handle
        val toChunk: net.minecraft.server.v1_8_R3.Chunk = toCC.handle
        val tsec: Array<ChunkSection> = fromChunk.sections
        val afterSec = arrayOfNulls<ChunkSection>(tsec.size)
        for (n in tsec.indices) {
            afterSec[n] = tsec[n]
        }
        toChunk.a(afterSec)
        toChunk.removeEntities()
        floor.world.refreshChunk(toChunk.bukkitChunk.x, toChunk.bukkitChunk.z)
    }

    fun destroy() {
        for(y in 0 until 16) {
            for (x in 0 until 16) {
                for (z in 0 until 16) {
                    var r: Block = floor.world.getBlockAt(this.x * 16 + x, 10 + y, this.z * 16 + z)
                    r.type = Material.AIR
                }
            }
        }
    }
}