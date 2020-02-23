package dev.dungeoncrawler.loot

enum class Prefix(val label: String, val multiplier: Double, val prefixRarity: PrefixRarity) {
	GODLY("Godly", 4.0, PrefixRarity.MYTHICAL),
	LEGENDARY("Legendary", 2.5, PrefixRarity.LEGENDARY),
	RARE("Rare", 1.5, PrefixRarity.RARE),
	SPECIAL("Special", 1.1, PrefixRarity.UNCOMMON),
	ODDLY_NORMAL("Oddly Normal", 1.01, PrefixRarity.UNCOMMON),
	RUSTY("Rusty", 0.75, PrefixRarity.COMMON),
	BROKEN("Broken", 0.25, PrefixRarity.COMMON)
}