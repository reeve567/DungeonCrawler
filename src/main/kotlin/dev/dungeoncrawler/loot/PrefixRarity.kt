package dev.dungeoncrawler.loot

enum class PrefixRarity(val chance: Double) {
	MYTHICAL(2.5),
	LEGENDARY(7.5),
	RARE(15.0),
	UNCOMMON(25.0),
	COMMON(50.0)
}