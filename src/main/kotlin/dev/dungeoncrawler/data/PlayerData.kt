package dev.dungeoncrawler.data

import dev.dungeoncrawler.dungeon.Party
import dev.dungeoncrawler.npcs.pets.Pet
import java.util.*

data class PlayerData(
		val id: UUID,
		var name: String,
		var balance: Int,
		var bankData: BankData,
		var marketData: MarketData,
		var level: Int,
		var exp: Long,
		var highestFloor: Int,
		val petLevels: PetLevelData
) {
	@Transient
	var party: Party? = null
	@Transient
	var pet: Pet? = null
	
}