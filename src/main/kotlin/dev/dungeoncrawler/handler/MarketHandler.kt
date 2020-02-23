package dev.dungeoncrawler.handler

import dev.dungeoncrawler.data.PlayerDataManager
import dev.dungeoncrawler.utility.clickableItem
import dev.dungeoncrawler.utility.gui
import dev.dungeoncrawler.utility.item
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.Comparator

class MarketHandler(private val playerDataManager: PlayerDataManager) : Listener {
	@EventHandler
	fun onMarketOpen(e: PlayerInteractEvent) {
		if (e.action == Action.RIGHT_CLICK_BLOCK) {
			if (e.clickedBlock.type == Material.TRAPPED_CHEST) {
				e.isCancelled = true
				market(1, SortType.DATE_ADDED, e.player)
				e.player.playSound(e.clickedBlock.location, Sound.CHEST_OPEN, .5f, .5f)
			}
		}
	}

	fun market(page: Int, sortType: SortType, player: Player) {
		gui {
			var temp: List<Triple<Date, Pair<Double, Boolean>, ItemStack>> = emptyList()
			playerDataManager.marketItems.values.forEach {
				it.forEach {
					temp.toMutableList().add(it)
				}
			}
			temp = temp.filter { it.second.second }

			temp.sortedWith(Comparator { o1, o2 ->
				when (sortType) {
					SortType.DATE_ADDED -> {
						when {
							o1.first.time > o2.first.time -> 1
							o1.first.time == o2.first.time -> 0
							else -> -1
						}
					}
					SortType.PRICE -> {
						when {
							o1.second.first < o2.second.first -> 1
							o1.second.first == o2.second.first -> 0
							else -> -1
						}
					}
				}
			})

			for (i in 0 until 45) {
				clickableItem {
					itemStack = item(temp[i].third) {

					}
				}
			}

		}.open(player)
	}

	enum class SortType {
		DATE_ADDED,
		PRICE
	}
}