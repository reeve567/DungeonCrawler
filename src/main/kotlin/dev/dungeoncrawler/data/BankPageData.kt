package dev.dungeoncrawler.data

import dev.dungeoncrawler.utility.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.pow

class BankPageData(var items: HashMap<Int, ItemStack> = HashMap()) {

	fun openPage(player: Player, playerDataManager: PlayerDataManager, currentPage: Int) {
		gui {
			clickableItem {
				itemStack = item(Material.STAINED_GLASS_PANE) {
					durability = 15
					itemMeta {
						displayName = " "
					}
				}
				val playerData = playerDataManager.playerData[player.uniqueId]!!
				if (currentPage == playerData.bankData.pages.size && currentPage < 5) {
					val cost = (currentPage + 1).toDouble().pow(2) * 100
					// buy option
					clickAction {
						if (playerData.balance >= cost) {
							playerDataManager.addBalance(player, -cost)
							playerData.bankData.pages[currentPage + 1] = BankPageData()
							playerData.bankData.pages[currentPage + 1]!!.openPage(player, playerDataManager, currentPage)
						}
						isCancelled = true
					}
					itemStack = item(Material.STAINED_GLASS_PANE) {
						if (playerData.balance >= cost) {
							durability = 5
						} else {
							durability = 15
						}
						itemMeta {
							displayName = "§6Buy page ${currentPage + 1}"
							lore = listOf("§7Cost $cost gold")
						}
					}


				} else if (currentPage != playerData.bankData.pages.size) {
					clickAction {
						openPage(player, playerDataManager, currentPage + 1)
						isCancelled = true
					}
					itemStack = item(Material.ARROW) {
						itemMeta {
							displayName = "§6Page ${currentPage + 1}"
						}
					}
				} else {
					clickAction {
						isCancelled = true
					}
				}
				slot = 53
			}


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