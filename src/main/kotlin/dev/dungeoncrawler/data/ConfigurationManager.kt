package dev.dungeoncrawler.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.*

class ConfigurationManager(private val playerDataManager: PlayerDataManager, dataFolder: File) {

	private val playerDataFile = File("playerData.json")
	private val balancesFile = File("balances.json")
	private val bankFile = File("bankData.json")

	private val gson = Gson()

	init {
		if (playerDataFile.exists()) {
			val bufferedReader = playerDataFile.bufferedReader()
			val inputString = bufferedReader.use { it.readText() }
			val type = object : TypeToken<HashMap<UUID, PlayerData>>() {}.type
			playerDataManager.playerData = gson.fromJson(inputString, type)
			println("PlayerData loaded!")
		} else {
			println("No PlayerData")
		}

		for (entry in playerDataManager.playerData) {
			entry.value.marketData.items?.also {
				playerDataManager.marketItems[entry.key] = it
			}
		}
	}

	fun save() {
		playerDataFile.writeText(gson.toJson(playerDataManager.playerData))
	}
}