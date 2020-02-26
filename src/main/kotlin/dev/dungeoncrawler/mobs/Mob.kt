package dev.dungeoncrawler.mobs

import dev.dungeoncrawler.dungeon.Floor
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Listener

open abstract class Mob(val loc: Location) {
    var entity: LivingEntity? = null

    abstract fun die()
}
