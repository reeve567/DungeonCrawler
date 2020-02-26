package dev.dungeoncrawler.mobs

import dev.dungeoncrawler.DungeonCrawler
import dev.dungeoncrawler.utility.Hologram
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import kotlin.random.Random
import kotlin.random.nextInt

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
		val chance = Random.nextInt(0..2)
		return if (chance == 0)
			spawn(Mite(loc, floor))
		else if (chance == 1)
			spawn(Undead(loc, floor))
		else
			spawn(Skeleton(loc, floor))
		
	}
	
	@EventHandler
	fun onDeath(e: EntityDeathEvent) {
		if (e.entity is LivingEntity) {
			val mob = isMob(e.entity) ?: return
			mob.die()
			mobs.remove(mob)
		}
	}
	
	@EventHandler
	fun onDamage(e: EntityDamageEvent) {
		if (e.entity is LivingEntity) {
			val mob = isMob(e.entity as LivingEntity) ?: return
			mob.onHit()
			
			val holo = Hologram("§c-${(e.finalDamage * 100).toInt() / 100.0}", e.entity.location.add(0.0, 1.5, 0.0))
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
				holo.entity.remove()
			}, 30L)
		}
	}
	
	fun isMob(e: LivingEntity): Mob? {
		return mobs.find { it.entity.uniqueId == e.uniqueId }
	}
}