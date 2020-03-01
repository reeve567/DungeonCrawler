package dev.dungeoncrawler.npcs.pets

import dev.dungeoncrawler.DungeonCrawler
import dev.dungeoncrawler.data.PlayerData
import dev.dungeoncrawler.extensions.asCraftPlayer
import net.minecraft.server.v1_8_R3.GenericAttributes
import net.minecraft.server.v1_8_R3.PathfinderGoalFollowOwner
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftWolf
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import kotlin.collections.HashMap

class Pet(private val owner: Player, val type: PetType, var playerData: PlayerData) : Listener {
	val entity: ArmorStand = Bukkit.getWorld("world").spawnEntity(owner.location, EntityType.ARMOR_STAND) as ArmorStand
	private val wolf: Wolf = Bukkit.getWorld("world").spawnEntity(owner.location, EntityType.WOLF) as Wolf
	var summoned = true
	var level = 1
	var exp = 0L
	private val tasks = ArrayList<Int>()
	
	companion object {
		val pets = HashMap<UUID, UUID>()
		val followers = HashMap<UUID, UUID>()
		
		fun getExpToLevel(level: Int): Long? {
			return when (level) {
				10 -> null
				1 -> 100
				else -> (getExpToLevel(level - 1)!! * 5)
			}
		}
	}
	
	init {
		Bukkit.getPluginManager().registerEvents(this, DungeonCrawler.instance)
		if (playerData.petLevels.levels.containsKey(type.id)) {
			level = playerData.petLevels.levels[type.id]!!.first
			exp = playerData.petLevels.levels[type.id]!!.second
		} else {
			saveLevels()
		}
		setPetEntity()
		setFollowerEntity()
		
		tasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(DungeonCrawler.instance, {
			if (summoned) {
				if (wolf.location.distance(owner.location) > 100) {
					// too far
					owner.sendMessage("§6You pet got too far away and returned.")
					remove()
				} else {
					var closestEntity: LivingEntity? = null
					var distance = Double.MAX_VALUE
					entity.getNearbyEntities(4.5, 4.0, 4.5).forEach {
						DungeonCrawler.instance.dungeon?.getRoom(entity.location.chunk.x, entity.location.chunk.z)?.also { room ->
							if (room.floor.mobs.containsKey(it.uniqueId)) {
								if (it is LivingEntity) {
									if (closestEntity != null) {
										if (it.location.distance(entity.location) < distance) {
											closestEntity = it
											distance = it.location.distance(entity.location)
										}
									} else {
										closestEntity = it
										distance = it.location.distance(entity.location)
									}
								}
							}
						}
					}
					if (closestEntity != null) {
						if (type.baseDamage > 0)
							closestEntity!!.damage(type.getDamageTotal(level), owner)
						type.attack(this, closestEntity!!)
					}
				}
			} else {
				if (!wolf.isDead) {
					remove()
				}
			}
		}, type.attackSpeed, type.attackSpeed))
		
		tasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(DungeonCrawler.instance, {
			if (summoned) {
				levelUp()
				entity.teleport(wolf.location.setDirection(owner.location.toVector().subtract(entity.location.toVector())).add(0.0, 2.0, 0.0), PlayerTeleportEvent.TeleportCause.PLUGIN)
			}
		}, 2L, 2L))
	}
	
	fun remove() {
		levelUp()
		saveLevels()
		
		pets.remove(entity.uniqueId)
		followers.remove(wolf.uniqueId)
		
		wolf.remove()
		entity.remove()
		summoned = false
		
		tasks.forEach { Bukkit.getScheduler().cancelTask(it) }
	}
	
	private fun saveLevels() {
		playerData.petLevels.levels[type.id] = level to exp
	}
	
	private fun levelUp() {
		if (getExpToLevel(level) != null)
			if (exp > getExpToLevel(level)!!) {
				exp -= getExpToLevel(level)!!
				level++
				saveLevels()
				owner.sendMessage("§6Your pet has leveled up to $level.")
				entity.customName = "§8[§6$level§8] §f${type.petName}"
			}
	}
	
	fun addExp(amount: Int) {
		exp += amount
	}
	
	private fun setPetEntity() {
		entity.setBasePlate(false)
		entity.setGravity(false)
		entity.isSmall = true
		entity.isVisible = false
		
		entity.customName = "§8[§6$level§8] §f${type.petName}"
		entity.isCustomNameVisible = true
		
		entity.setMetadata("pet", FixedMetadataValue(DungeonCrawler.instance, owner.uniqueId.toString()))
		entity.equipment.helmet = type.getHead()
		pets[entity.uniqueId] = owner.uniqueId
	}
	
	private fun setFollowerEntity() {
		wolf.isTamed = true
		wolf.owner = owner
		val goalSelector = PathfinderGoalSelector((owner.world as CraftWorld).handle.methodProfiler)
		goalSelector.a(5, PathfinderGoalFollowOwner((wolf as CraftWolf).handle, 1.0, 2f, 2f))
		wolf.handle.goalSelector = goalSelector
		
		wolf.handle.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).value = .45
		wolf.handle.b(true)
		
		wolf.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1, false, false))
		wolf.setMetadata("follower", FixedMetadataValue(DungeonCrawler.instance, owner.uniqueId.toString()))
		pets[wolf.uniqueId] = owner.uniqueId
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	fun onDamage(e: EntityDamageEvent) {
		if (e.entity.hasMetadata("pet") || e.entity.hasMetadata("follower") || followers.containsKey(e.entity.uniqueId) || pets.containsKey(e.entity.uniqueId))
			e.isCancelled = true
	}
	
	@EventHandler
	fun onPlayerDamaged(e: EntityDamageByEntityEvent) {
		if (e.damager.hasMetadata("follower") || followers.containsKey(e.damager.uniqueId))
			e.isCancelled = true
	}
	
	
}