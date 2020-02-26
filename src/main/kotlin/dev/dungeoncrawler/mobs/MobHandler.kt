package dev.dungeoncrawler.mobs

import dev.dungeoncrawler.DungeonCrawler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class MobHandler(val plugin: DungeonCrawler) : Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    val mobs: ArrayList<Mob> = ArrayList()
    val world: World = Bukkit.getWorld("world")

    fun spawn(mob: Mob): Mob {
        mobs.add(mob)
        return mob
    }

    fun spawnRandomMob(floor: Int, loc: Location): Mob? {
        if (floor == 1) {
            val chance = Math.random()
            if (chance < 0.5)
                return spawn(Mite(loc))
            else
                return spawn(Undead(loc))
        }
        return null
    }

    @EventHandler
    fun onDeath(e: EntityDeathEvent) {
        if (e.entity is LivingEntity) {
            val m = isMob(e.entity) ?: return
            m.die()
            mobs.remove(m)
        }
    }

    fun isMob(e: LivingEntity): Mob? {
        for (i in 0..mobs.size) {
            if (mobs[i].entity == e) {
                return mobs[i]
            }
        }
        return null
    }
}