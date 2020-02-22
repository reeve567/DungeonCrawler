package dev.dungeoncrawler.dungeon

import dev.dungeoncrawler.Constants
import org.bukkit.Bukkit
import org.bukkit.World
import kotlin.random.Random
import kotlin.random.nextInt

class Floor(val dungeon: Dungeon, val maxRooms: Int) {

	val world: World = Bukkit.getWorld("world")
	private val rooms: ArrayList<Room> = ArrayList()

	fun destroy() {
		for (room in rooms) {
			room.destroy()
		}
		rooms.clear()
	}

	// devote themselves to a direction and make most chance go to straight
	// maybe pass on chance value and make it smaller if it's going in another direction from straight

	// generate in squares
	// first square of 8 - generate 6
	// second square of 18 - generate 10 & keep generating if not next to one in first square yet
	// etc

	fun createRooms(rings: Int) {
		val rooms = HashMap<Pair<Int, Int>, Room>()
		rooms[Pair(0, 0)] = createRoom(0, 0, 0)
		fun create(roomsSize: Int, squareSize: Int) {
			if (rooms.size != 1)
				rooms.clear()
			while (rooms.size < roomsSize) {
				val x = Random.nextInt(IntRange(-squareSize, squareSize))
				val z = Random.nextInt(IntRange(-squareSize, squareSize))
				if (x == -squareSize || x == squareSize) {
					if (!rooms.containsKey(Pair(x, z)))
						rooms[Pair(x, z)] = createRoom(x, z, roomsSize * 5)

				} else if (z == -squareSize || z == squareSize) {
					if (!rooms.containsKey(Pair(x, z)))
						rooms[Pair(x, z)] = createRoom(x, z, roomsSize * 5)
				}
			}
			for (roomEntry in rooms) {
				this.rooms.add(roomEntry.value)
			}
		}

		for (i in 1..rings) {
			create((i * 4) + 2 + (if (i == 1) 1 else 0) + (if (i >= 4) 2 else 0), i)
		}

		for (room in this.rooms) {
			room.createDoors()
		}
	}

	fun createRoom(x: Int, z: Int, delay: Int): Room {
		val pfbIndex: Int = (Math.random() * (Constants.PREFAB_SIZE * Constants.PREFAB_SIZE)).toInt()
		val room = Room(this, x, z, pfbIndex)
		room.create(delay)
		return room
	}

	fun roomExists(x: Int, z: Int): Boolean {
		for (room in rooms) {
			if (room.x == x && room.z == z)
				return true
		}
		return false
	}

	fun roomExistsAroundWithin(x: Int, z: Int, x2: Int, z2: Int): Boolean {
		fun roomCheck(x: Int, z: Int): Boolean {
			if (roomExists(x, z)) {
				if (x <= x2 && x >= -x2) {
					if (z <= z2 && z >= -z2) {
						return true
					}
				}
			}
			return false
		}

		if (roomCheck(x + 1, z)) return true
		if (roomCheck(x - 1, z)) return true
		if (roomCheck(x, z + 1)) return true
		if (roomCheck(x, z - 1)) return true
		return false
	}

	enum class Direction {
		NORTH,
		EAST,
		SOUTH,
		WEST,
		NONE
	}
}