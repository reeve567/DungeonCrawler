package dev.dungeoncrawler.mobs

import dev.dungeoncrawler.dungeon.Floor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Silverfish
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

class Undead(loc: Location) : Mob(loc) {
    init {
        val entity: Zombie = loc.world.spawn(loc, Zombie::class.java)
        entity.customName = "§cUndead"
        entity.isCustomNameVisible = true
        entity.maxHealth = 10.0
        entity.health = 10.0
        entity.equipment.helmet = ItemStack(Material.STONE_BUTTON)
        entity.equipment.helmetDropChance = 0f
        this.entity = entity
    }

    override fun die() {

    }
}