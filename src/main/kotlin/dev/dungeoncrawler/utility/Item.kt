package dev.dungeoncrawler.utility

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

fun item(material: Material, block: ItemStack.() -> Unit) : ItemStack = ItemStack(material).apply(block)

fun item(item: ItemStack, block: ItemStack.() -> Unit) : ItemStack = item.apply(block)

fun ItemStack.itemMeta(block: ItemMeta.() -> Unit) : ItemMeta {
	val new = itemMeta.apply(block)
	itemMeta = new
	return itemMeta
}