package dev.dungeoncrawler.loot.crate

interface PrizeContainer {
	fun getPrizes(): List<Pair<Prize<*>, Int>>
}