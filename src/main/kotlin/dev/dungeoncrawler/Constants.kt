package dev.dungeoncrawler

import org.bukkit.Bukkit
import org.bukkit.Location

object Constants {
	const val PREFAB_SIZE = 3
	val SPAWN_LOCATION = Location(Bukkit.getWorld("world"), 10000.5, 41.0, 10000.5)

	object SpawnPortal {
		object CornerOne {
			const val X = 10016
			const val Z = 9999
		}

		object CornerTwo {
			const val X = 10018
			const val Z = 10001
		}
	}
}