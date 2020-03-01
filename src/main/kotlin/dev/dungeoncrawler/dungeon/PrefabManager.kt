package dev.dungeoncrawler.dungeon

object PrefabManager {
	val prefabList = ArrayList<Prefab>()
	val empty = Prefab(999, 999, PrefabDirectionalInformation(false, false, false, false))
	val covered = Prefab(998, 999, PrefabDirectionalInformation(false, false, false, false))
	val checkpoint = Prefab(998, 998, PrefabDirectionalInformation(true, true, true, true))
	
	init {
		prefabList.addAll(
				listOf(
						// full intersection
						Prefab(1000, 1000, PrefabDirectionalInformation(true, true, true, true)),
						Prefab(1001, 1000, PrefabDirectionalInformation(true, true, true, true)),
						Prefab(1002, 1000, PrefabDirectionalInformation(true, true, true, true)),
						Prefab(1000, 1001, PrefabDirectionalInformation(true, true, true, true)),
						Prefab(1001, 1001, PrefabDirectionalInformation(true, true, true, true)),
						Prefab(1002, 1001, PrefabDirectionalInformation(true, true, true, true)),
						Prefab(1000, 1002, PrefabDirectionalInformation(true, true, true, true)),
						Prefab(1001, 1002, PrefabDirectionalInformation(true, true, true, true)),
						Prefab(1002, 1002, PrefabDirectionalInformation(true, true, true, true)),
						
						// hallway
						Prefab(1000, 1003, PrefabDirectionalInformation(false, false, true, true)),
						Prefab(1001, 1003, PrefabDirectionalInformation(true, true, false, false)),
						
						// dead end
						Prefab(1000, 1005, PrefabDirectionalInformation(true, false, false, false)),
						Prefab(1001, 1005, PrefabDirectionalInformation(false, false, true, false)),
						Prefab(1002, 1005, PrefabDirectionalInformation(false, true, false, false)),
						Prefab(1003, 1005, PrefabDirectionalInformation(false, false, false, true)),
						
						// three way
						Prefab(1000, 1007, PrefabDirectionalInformation(true, false, true, true)),
						Prefab(1001, 1007, PrefabDirectionalInformation(true, true, true, false)),
						Prefab(1002, 1007, PrefabDirectionalInformation(false,true, true, true)),
						Prefab(1003, 1007, PrefabDirectionalInformation(true, true, false, true)),
						
						// none
						covered
				)
		)
	}
	
	fun getPrefab(directionalInformation: PrefabDirectionalInformation): Prefab {
		
		
		prefabList.filter {
			/**/
			directionalInformation.north == it.directionalInformation.north && directionalInformation.south == it.directionalInformation.south && directionalInformation.east == it.directionalInformation.east && directionalInformation.west == it.directionalInformation.west
		}.also {
			if (it.isNotEmpty())
				return it.random()
			else {
				return prefabList.filter { prefab ->
					var hasNorth = true
					var hasSouth = true
					var hasEast = true
					var hasWest = true
					if (directionalInformation.north) {
						hasNorth = prefab.directionalInformation.north
					}
					if (directionalInformation.south) {
						hasSouth = prefab.directionalInformation.south
					}
					if (directionalInformation.east) {
						hasEast = prefab.directionalInformation.east
					}
					if (directionalInformation.west) {
						hasWest = prefab.directionalInformation.west
					}
					
					hasNorth && hasSouth && hasEast && hasWest
				}.random()
			}
		}
	}
}