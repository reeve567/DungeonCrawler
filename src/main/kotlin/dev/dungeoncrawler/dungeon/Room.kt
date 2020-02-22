package dev.dungeoncrawler.dungeon

import dev.dungeoncrawler.Constants
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.material.MaterialData

class Room(private val floor : Floor, val x: Int, val z: Int, private val pfbIndex: Int) {
    fun create() {
        val pfbX = (pfbIndex / Constants.PREFAB_SIZE)
        val pfbZ = (pfbIndex % Constants.PREFAB_SIZE)
        val cx = (1000 + pfbX) * 16
        val cz = (1000 + pfbZ) * 16
        for(y in 0 until 16) {
            for (x in 0 until 16) {
                for (z in 0 until 16) {
                    var b : Block = floor.world.getBlockAt(cx + x, 10 + y, cz + z)
                    var r : Block = floor.world.getBlockAt(this.x * 16 + x, 10 + y, this.z * 16 + z)
                    r.type = b.type
                    r.state.data = b.state.data
                    r.state.update(true)
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