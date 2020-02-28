package dev.dungeoncrawler.utility

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

fun item(material: Material, block: ItemStack.() -> Unit): ItemStack = ItemStack(material).apply(block)

fun item(item: ItemStack, block: ItemStack.() -> Unit): ItemStack = item.apply(block)

fun head(tag: String): ItemStack {
	val itemStack = ItemStack(Material.SKULL_ITEM, 1, 3)
	val meta = itemStack.itemMeta
	
	val profile = GameProfile(UUID.randomUUID(), "")
	profile.properties.put("textures", Property("textures", tag))
	
	val profileField = meta.javaClass.getDeclaredField("profile")
	profileField.isAccessible = true
	profileField.set(meta, profile)
	
	itemStack.itemMeta = meta
	
	return itemStack
}

fun ItemStack.itemMeta(block: ItemMeta.() -> Unit): ItemMeta {
	val new = itemMeta.apply(block)
	itemMeta = new
	return itemMeta
}