package dev.dungeoncrawler

import org.bukkit.Bukkit
import org.bukkit.Location

object Constants {
	const val PREFAB_SIZE = 3
	val SPAWN_LOCATION = Location(Bukkit.getWorld("world"), 10000.5, 41.0, 10000.5)
	const val CHEST_SPAWN_CHANCE = 50
	const val CHEST_MIN_LOOT = 1
	const val CHEST_MAX_LOOT = 5

	const val MOB_SPAWN_MIN = 2
	const val MOB_SPAWN_MAX = 4
	
	const val CHECKPOINT_COUNT = 2

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

	enum class RankTeam(val label: String, val prefix: String, val permission: String) {
		DEVELOPER("a_developers", "§b§lDEV §r", "dungeoncrawler.developer"),
		HELPER("b_helpers", "§a§lHELPER §r", "dungeoncrawler.helper"),
		TESTER("c_testers", "§c§lTESTER §r", "dungeoncrawler.tester"),
		MEMBER("d_members", "§7&lMEMBER §r", "dungeoncrawler.member")
	}
}