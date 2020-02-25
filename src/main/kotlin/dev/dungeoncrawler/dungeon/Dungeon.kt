package dev.dungeoncrawler.dungeon

import dev.dungeoncrawler.DungeonCrawler
import dev.dungeoncrawler.data.PlayerDataManager
import org.bukkit.Bukkit
import org.bukkit.Chunk

class Dungeon(val plugin: DungeonCrawler, val playerDataManager: PlayerDataManager) {
	
	val floors: Array<Floor?> = arrayOfNulls(5)
	
	init {
		for (i in floors.indices) {
			floors[i] = Floor(this, i + 1, i * -25, i != floors.size - 1)
			Bukkit.getPluginManager().registerEvents(floors[i], plugin)
		}
	}
	
	fun generate() {
		for (floor in floors) {
			floor?.createRooms(6)
		}
	}
	
	fun getRoom(x: Int, z: Int): Room? {
		floors.forEach {
			if (it?.getRoom(x, z) != null)
				return it.getRoom(x, z)
		}
		return null
	}
	
	fun destroy() {
		for (floor in floors) {
			floor?.destroy()
		}
	}
}