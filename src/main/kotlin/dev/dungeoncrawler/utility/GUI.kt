package dev.dungeoncrawler.utility

import dev.dungeoncrawler.DungeonCrawler
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class GUI(val size: Int = 54, val items: ArrayList<ClickableItem> = ArrayList()) : Listener {
	val inv: Inventory = Bukkit.createInventory(null, size, "Inventory")
	var onClose: (InventoryCloseEvent.() -> Unit)? = null

	init {
		Bukkit.getPluginManager().registerEvents(this, DungeonCrawler.instance)
	}

	fun add(clickableItem: ClickableItem) {
		items.add(clickableItem)
		clickableItem.slot?.let { inv.setItem(it, clickableItem.itemStack) }
	}

	fun open(player: Player) {
		player.openInventory(inv)
	}

	@EventHandler
	fun onClose(e: InventoryCloseEvent) {
		if (e.inventory.hashCode() == inv.hashCode()) {
			onClose?.also {
				it.invoke(e)
			}
			for (item in items) {
				InventoryClickEvent.getHandlerList().unregister(item)
			}
		}
	}
}

class ClickableItem(var itemStack: ItemStack? = null, var slot: Int? = null, var click: ClickAction? = null) :
		Listener {
	init {
		Bukkit.getPluginManager().registerEvents(this, DungeonCrawler.instance)
	}

	@EventHandler
	fun onEvent(e: InventoryClickEvent) {
		try {
			if (e.currentItem != null)
				if (e.currentItem.type == itemStack!!.type && e.currentItem.durability == itemStack!!.durability && e.currentItem.amount == itemStack!!.amount && e.currentItem.hasItemMeta() == itemStack!!.hasItemMeta()) {
					if (itemStack!!.hasItemMeta()) {
						if (e.currentItem.itemMeta.displayName == itemStack!!.itemMeta.displayName && e.currentItem.itemMeta.lore == itemStack!!.itemMeta.lore)
							click?.action?.let { e.apply(it) }
					} else {
						click?.action?.let { e.apply(it) }
					}
				}
		} catch (e: NullPointerException) {
			println("someone made an oopsies")
			e.printStackTrace()
		}

	}
}

open class ClickAction(val action: InventoryClickEvent.() -> Unit)

class NoClickAction : ClickAction({ isCancelled = true })

class OpenInventoryClickAction(val inventory: Inventory) : ClickAction({
	this.inventory
})

fun gui(size: Int = 54, block: GUI.() -> Unit): GUI = GUI(size).apply(block)

fun GUI.clickableItem(block: ClickableItem.() -> Unit) {
	val clickableItem = ClickableItem()
	clickableItem.apply(block)
	this.add(clickableItem)
}

fun GUI.item(itemStack: ItemStack) {
	this.inv.addItem(itemStack)
}

fun GUI.item(index: Int, itemStack: ItemStack) {
	this.inv.setItem(index, itemStack)
}

fun GUI.onClose(block: InventoryCloseEvent.() -> Unit) {
	onClose = block
}

fun ClickableItem.clickAction(block: InventoryClickEvent.() -> Unit): ClickAction {
	val clickAction = ClickAction(block)
	click = clickAction
	return clickAction
}