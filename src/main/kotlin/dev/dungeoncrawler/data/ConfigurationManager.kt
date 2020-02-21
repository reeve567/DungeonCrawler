package dev.dungeoncrawler.data

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dev.reeve.quests.item
import dev.reeve.quests.itemMeta
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*

class ConfigurationManager(private val playerDataManager: PlayerDataManager, dataFolder: File) {

	private val playerDataFile = File(dataFolder, "playerData.json")
	private val balancesFile = File(dataFolder, "balances.json")
	private val bankFile = File(dataFolder, "bankData.json")

	private val gson = GsonBuilder().setPrettyPrinting().registerTypeAdapter(ItemStack::class.java, ItemStackTypeAdapter()).create()

	init {
		if (playerDataFile.exists()) {
			val bufferedReader = playerDataFile.bufferedReader()
			val inputString = bufferedReader.use { it.readText() }
			val type = object : TypeToken<HashMap<UUID, PlayerData>>() {}.type
			playerDataManager.playerData = gson.fromJson(inputString, type)
			println("PlayerData loaded!")
		} else {
			println("No PlayerData")
			playerDataFile.mkdirs()
			playerDataFile.createNewFile()
		}

		for (entry in playerDataManager.playerData) {
			entry.value.marketData.items?.also {
				playerDataManager.marketItems[entry.key] = it
			}
		}
	}

	fun save() {
		println(gson.toJson(playerDataManager.playerData))
		playerDataFile.writeText(gson.toJson(playerDataManager.playerData))
	}
}