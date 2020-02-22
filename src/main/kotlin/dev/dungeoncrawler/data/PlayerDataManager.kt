package dev.dungeoncrawler.data

import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

class PlayerDataManager {

	var playerData: HashMap<UUID, PlayerData> = HashMap()
	var marketItems = HashMap<UUID, ArrayList<Triple<Date, Pair<Double, Boolean>, ItemStack>>>()

}