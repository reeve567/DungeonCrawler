package dev.dungeoncrawler.data

import org.bukkit.inventory.ItemStack

class BankPageData(itemsSerialized: HashMap<Int, Map<String, Any>>? = null) {
	val items = HashMap<Int, ItemStack>()

	init {
		itemsSerialized?.also {
			for (entry in itemsSerialized) {
				items[entry.key] = ItemStack.deserialize(entry.value)
			}
		}
	}

	fun serialize(): HashMap<Int, Map<String, Any>> {
		val hash = HashMap<Int, Map<String, Any>>()
		for (entry in items) {
			hash[entry.key] = entry.value.serialize()
		}
		return hash
	}

}