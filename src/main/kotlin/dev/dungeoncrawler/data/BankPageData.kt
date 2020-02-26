package dev.dungeoncrawler.data

import dev.dungeoncrawler.utility.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.pow

class BankPageData(var items: HashMap<Int, ItemStack> = HashMap()) {

	fun openPage(player: Player, playerDataManager: PlayerDataManager, currentPage: Int) {
		gui {
			var count = 0
			val playerData = playerDataManager.playerData[player.uniqueId]!!
			for (item in this@BankPageData.items) {
				item(item.key, item.value)
				count += item.value.amount
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

			clickableItem {
				if (currentPage == playerData.bankData.pages.size && currentPage < 5) {
					val cost = (currentPage + 1).toDouble().pow(2) * 100
					// buy option
					clickAction {
						if (playerData.balance >= cost) {
							playerDataManager.addBalance(player, -cost.toInt())
							playerData.bankData.pages[currentPage + 1] = BankPageData()
							playerData.bankData.pages[currentPage + 1]!!.openPage(player, playerDataManager, currentPage + 1)
						}
						isCancelled = true
					}
					itemStack = item(Material.STAINED_GLASS_PANE) {
						durability = if (playerData.balance >= cost) {
							5
						} else {
							15
						}
						itemMeta {
							displayName = "§6Buy page ${currentPage + 1}"
							lore = listOf("§7Cost $cost gold")
						}
					}


				} else if (currentPage != playerData.bankData.pages.size) {
					clickAction {
						playerData.bankData.pages[currentPage + 1]!!.openPage(player, playerDataManager, currentPage + 1)
						isCancelled = true
					}
					itemStack = item(Material.ARROW) {
						itemMeta {
							displayName = "§6Page ${currentPage + 1}"
						}
					}
				} else {
					itemStack = item(Material.STAINED_GLASS_PANE) {
						durability = 15
						itemMeta {
							displayName = " "
						}
					}

					clickAction {
						isCancelled = true
					}
				}
				slot = 53
			}

			if (currentPage > 1) {
				clickableItem {
					clickAction {
						playerData.bankData.pages[currentPage - 1]!!.openPage(player, playerDataManager, currentPage - 1)
						isCancelled = true
					}

					itemStack = item(Material.ARROW) {
						itemMeta {
							displayName = "§6Page ${currentPage - 1}"
						}
					}

					slot = 45
				}
			}

			clickableItem {
				itemStack = item(Material.BOOK) {
					itemMeta {
						displayName = "§6Page $currentPage"
						lore = listOf("§7Total pages: ${playerData.bankData.pages.size}", "§7Items: $count", "§7Balance: ${playerData.balance}")
					}
				}
				slot = 49
				clickAction { isCancelled = true }
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