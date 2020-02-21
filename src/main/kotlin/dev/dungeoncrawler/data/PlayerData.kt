package dev.dungeoncrawler.data

import java.util.*

data class PlayerData(val id: UUID, var name: String, var balance: Double, var bankData: BankData, var marketData: MarketData)