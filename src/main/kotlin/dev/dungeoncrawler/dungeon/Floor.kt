package dev.dungeoncrawler.dungeon

import dev.dungeoncrawler.Constants
import org.bukkit.Bukkit
import org.bukkit.World

class Floor(val dungeon: Dungeon, val maxRooms: Int) {


    val world: World = Bukkit.getWorld("world")
    val rooms: ArrayList<Room> = ArrayList()

    init {
    }

    fun destroy() {
        for(room in rooms) {
            room.destroy()
        }
        rooms.clear()
    }

    fun createRoom(x: Int, z: Int) {
        if (rooms.size >= maxRooms)
            return;
        val pfbIndex: Int = (Math.random() * (Constants.PREFAB_SIZE * Constants.PREFAB_SIZE)) as Int
        val r = Room(this, x, z, pfbIndex)
        val chance: Double = 1 - ((rooms.size as Double / maxRooms) / 2.0)
        if (Math.random() < chance)
            if (!roomExists(x + 1, z))
                createRoom(x + 1, z)
        if (Math.random() < chance)
            if (!roomExists(x - 1, z))
                createRoom(x - 1, z)
        if (Math.random() < chance)
            if (!roomExists(x, z + 1))
                createRoom(x, z + 1)
        if (Math.random() < chance)
            if (!roomExists(x, z - 1))
                createRoom(x, z - 1)
    }

    fun roomExists(x: Int, z: Int): Boolean {
        for (room in rooms) {
            if (room.x == x && room.z == z)
                return true;
        }
        return false;
    }
}