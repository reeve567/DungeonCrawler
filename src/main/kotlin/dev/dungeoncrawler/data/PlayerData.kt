package dev.dungeoncrawler.data

import java.util.*

data class PlayerData(val id: UUID, var name: String, var balance: Int, var bankData: BankData, var marketData: MarketData, var level: Int, var exp: Int, var highestFloor: Int)