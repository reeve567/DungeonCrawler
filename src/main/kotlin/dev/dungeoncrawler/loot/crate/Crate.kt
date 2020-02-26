package dev.dungeoncrawler.loot.crate

import dev.dungeoncrawler.npcs.pets.PetType
import dev.dungeoncrawler.utility.Hologram
import dev.dungeoncrawler.utility.item
import dev.dungeoncrawler.utility.itemMeta
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

enum class Crate(val label: String, val location: Location) : PrizeContainer {
	VOTE("§aVote", Location(Bukkit.getWorld("world"), 9985.0, 42.0, 10007.0)) {
		override fun getPrizes(): List<Pair<Prize<*>, Int>> {
			val list = ArrayList<Pair<Prize<*>, Int>>()
			list.add(Prize(500) to 50)
			list.add(Prize(PetType.BEE) to 10)
			list.add(Prize(item(Material.DIAMOND_SWORD) { addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 10) }, "Diamond Sword") to 40)
			return list
		}
	}
	
	;
	
	fun getKey(): ItemStack {
		return item(Material.TRIPWIRE_HOOK) {
			itemMeta {
				displayName = "$label §rKey"
				lore = listOf("§7Use this at spawn to get a prize!")
			}
		}
	}
	
	fun initialize() {
		Hologram("$label §rCrate", location.clone().add(0.5, 1.0, 0.5))
	}
}