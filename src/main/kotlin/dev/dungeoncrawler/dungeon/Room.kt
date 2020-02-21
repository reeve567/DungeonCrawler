package dev.dungeoncrawler.dungeon

import dev.dungeoncrawler.Constants
import org.bukkit.*
import org.bukkit.block.Block

class Room(private val floor : Floor, val x: Int, val z: Int, private val pfbIndex: Int) {
    fun create() {
        val pfbX = (pfbIndex / Constants.PREFAB_SIZE)
        var pfbZ = (pfbIndex % Constants.PREFAB_SIZE)
        val chunk : Chunk = floor.world.getChunkAt(1000 + pfbX, 1000 + pfbZ)
        for(y in 0 until 16) {
            for (x in 0 until 16) {
                for (z in 0 until 16) {
                    var b : Block = floor.world.getBlockAt(chunk.x * 16 + x, 10 + y, chunk.z + z)
                    var r : Block = floor.world.getBlockAt(this.x * 16 + x, 10 + y, this.z * 16 + z)
                    r.state.data = b.state.data
                    r.state.update()
                }
            }
        }
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