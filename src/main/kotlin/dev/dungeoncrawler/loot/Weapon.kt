package dev.dungeoncrawler.loot

import kotlin.random.Random

class Weapon(prefix: Prefix? = null, damage: Double? = null, floor: Int? = null) {
	val prefix: Prefix
	//val damage: Double
	//val material: Material

	init {
		if (prefix == null) {
			val rand = Random.nextDouble(0.0, 100.0)
			var chance = PrefixRarity.COMMON.chance
			val values = if (rand < chance) {
				Prefix.values().filter { it.prefixRarity == PrefixRarity.COMMON }
			} else {
				chance += PrefixRarity.UNCOMMON.chance
				if (rand < chance) {
					Prefix.values().filter { it.prefixRarity == PrefixRarity.UNCOMMON }
				} else {
					chance += PrefixRarity.RARE.chance
					if (rand < chance) {
						Prefix.values().filter { it.prefixRarity == PrefixRarity.RARE }
					} else {
						chance += PrefixRarity.LEGENDARY.chance
						if (rand < chance) {
							Prefix.values().filter { it.prefixRarity == PrefixRarity.LEGENDARY }
						} else {
							Prefix.values().filter { it.prefixRarity == PrefixRarity.MYTHICAL }
						}
					}
				}
			}
			this.prefix = values[Random.nextInt(values.size)]
		} else this.prefix = prefix
		/*if (damage == null) {
			if (floor == null) {

			} else {

			}
		} else this.damage = damage*/
	}

	/*fun build(): ItemStack {
		return item(material) {

		}
	}*/
}