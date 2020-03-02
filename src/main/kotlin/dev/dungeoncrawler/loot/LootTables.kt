package dev.dungeoncrawler.loot

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.math.min

object LootTables {

    // Loot tables are in ascending order of rarity.

    val floorOne: Array<ItemStack> = arrayOf(ItemStack(Material.APPLE), ItemStack(Material.BREAD), ItemStack(Material.COOKIE), ItemStack(Material.WOOD_AXE),
            ItemStack(Material.WOOD_SWORD), ItemStack(Material.STONE_SWORD), ItemStack(Material.LEATHER_CHESTPLATE), ItemStack(Material.IRON_AXE))

    val lootTables: Array<Array<ItemStack>> = arrayOf(floorOne)

    fun getRandomItem(floor: Int, multiplier: Double): ItemStack {
        return lootTables[floor - 1][(min(Math.random() * multiplier, 1.0) * lootTables[floor - 1].size).toInt() - 1]
    }
}