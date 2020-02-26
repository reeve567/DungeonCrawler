package dev.dungeoncrawler.mobs

import dev.dungeoncrawler.dungeon.Floor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Silverfish
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

class Mite(loc: Location) : Mob(loc) {
    init {
        val entity: Silverfish = loc.world.spawn(loc, Silverfish::class.java)
        entity.customName = "§cMite"
        entity.isCustomNameVisible = true
        entity.maxHealth = 4.0
        entity.health = 4.0
        entity.equipment.helmet = ItemStack(Material.STONE_BUTTON)
        entity.equipment.helmetDropChance = 0f
        this.entity = entity
    }

    override fun die() {

    }
}