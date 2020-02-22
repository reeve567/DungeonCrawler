package dev.dungeoncrawler.dungeon

import dev.dungeoncrawler.DungeonCrawler

class Dungeon(val dc : DungeonCrawler) {

    val floors : Array<Floor?> = arrayOfNulls(1)

    init {
        for(i in floors.indices) {
            floors[i] = Floor(this, 20)
        }
    }

    fun generate() {
        for(floor in floors) {
            floor?.createRooms()
        }
    }

    fun destroy() {
        for(floor in floors) {
            floor?.destroy()
        }
    }
}