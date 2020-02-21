package dev.dungeoncrawler.data

import org.bson.Document
import java.util.*
import kotlin.collections.HashMap

class PlayerDataManager {

	private val playerData: HashMap<UUID, PlayerData> = HashMap()
	private val databaseHandler = DatabaseHandler()

	fun saveAndRemove(id: UUID): Boolean {
		if (playerData[id] == null)
			return false
		val playerData = this.playerData[id]!!

		val document = Document()
		document["id"] = id.toString()
		document["name"] = playerData.name
		document["balance"] = playerData.balance
		document["bankData"] = playerData.bankData.serialize()

		databaseHandler.insertOrUpdatePlayers(id.toString(), document)
		this.playerData.remove(id)
		return true
	}

	fun load(id: UUID, name: String) {
		if (databaseHandler.hasPlayersDocument(id.toString())) {
			val document = databaseHandler.getPlayersDocument(id.toString())!!
			playerData[id] = PlayerData(id, name, document.getDouble("balance"), BankData(document.get(document["bankData"], HashMap<Int, HashMap<Int, Map<String, Any>>>().javaClass)))
		} else {
			playerData[id] = PlayerData(id, name, 0.0, BankData())
		}
	}

}