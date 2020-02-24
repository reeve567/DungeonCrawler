package dev.dungeoncrawler.dungeon

import dev.dungeoncrawler.DungeonCrawler
import dev.dungeoncrawler.data.PlayerDataManager
import org.bukkit.Bukkit

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
	
	fun destroy() {
		for (floor in floors) {
			floor?.destroy()
		}
	}
}