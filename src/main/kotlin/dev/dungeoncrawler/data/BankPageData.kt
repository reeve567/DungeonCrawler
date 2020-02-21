package dev.dungeoncrawler.data

import dev.reeve.quests.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BankPageData(var items: HashMap<Int, ItemStack> = HashMap()) {

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