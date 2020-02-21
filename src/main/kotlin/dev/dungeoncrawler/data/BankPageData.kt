package dev.dungeoncrawler.data

import dev.reeve.quests.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BankPageData(itemsSerialized: Map<String, Map<String, Any>>? = null) {
	var items = HashMap<Int, ItemStack>()

	init {
		itemsSerialized?.also {
			for (entry in itemsSerialized) {
				items[Integer.valueOf(entry.key)] = ItemStack.deserialize(entry.value)
			}
		}
	}

	fun serialize(): HashMap<String, Map<String, Any>> {
		val hash = HashMap<String, Map<String, Any>>()
		for (entry in items) {
			hash[entry.key.toString()] = entry.value.serialize()
		}
		return hash
	}

	fun openPage(player: Player) {
		gui {
			for (i in 45 until 54) {
				clickableItem {
					clickAction { isCancelled = true }
					itemStack = item(Material.STAINED_GLASS_PANE) {
						durability = 15
						itemMeta {
							displayName = " "
						}
					}
					slot = i
				}
			}
			for (item in this@BankPageData.items) {
				item(item.key, item.value)
			}
			onClose {
				this@BankPageData.items = HashMap()
				for (i in 0 until 45) {
					if (inventory.getItem(i) != null && inventory.getItem(i).type != Material.AIR) {
						this@BankPageData.items[i] = inventory.getItem(i)
					}
				}
			}
		}.open(player)
	}

}