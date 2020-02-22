package dev.dungeoncrawler.dungeon

import dev.dungeoncrawler.Constants
import org.bukkit.Bukkit
import org.bukkit.World

class Floor(val dungeon: Dungeon, val maxRooms: Int) {


    val world: World = Bukkit.getWorld("world")
    private val rooms: ArrayList<Room> = ArrayList()

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
        val pfbIndex: Int = (Math.random() * (Constants.PREFAB_SIZE * Constants.PREFAB_SIZE)).toInt()
        val r = Room(this, x, z, pfbIndex)
        r.create()
        rooms.add(r)
        val chance: Double = 1 - ((rooms.size.toDouble() / maxRooms / 4.0) / 2.0)

	    fun tryCreateRoom(chance : Double,x: Int, z: Int) {
		    if (Math.random() < chance || (x == 0 && z == 0))
			    if (!roomExists(x, z))
				    createRoom(x, z)
	    }

	    // devote themselves to a direction and make most chance go to straight
	    // maybe pass on chance value and make it smaller if it's going in another direction from straight

	    // east
	    tryCreateRoom(chance, x + 1, z)
		// west
	    tryCreateRoom(chance, x - 1, z)
	    // south
	    tryCreateRoom(chance, x, z + 1)
	    // north
	    tryCreateRoom(chance, x, z - 1)
    }

    fun roomExists(x: Int, z: Int): Boolean {
        for (room in rooms) {
            if (room.x == x && room.z == z)
                return true;
        }
        return false;
    }

	enum class Direction {
		NORTH,
		EAST,
		SOUTH,
		WEST,
		NONE
	}
}