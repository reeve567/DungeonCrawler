package dev.dungeoncrawler.npcs.pets

import dev.dungeoncrawler.DungeonCrawler
import dev.dungeoncrawler.data.PlayerData
import dev.dungeoncrawler.extensions.asCraftPlayer
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftZombie
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

class Pet(val owner: Player, val type: PetType, var playerData: PlayerData) : Listener {
	val entity: ArmorStand = Bukkit.getWorld("world").spawnEntity(owner.location, EntityType.ARMOR_STAND) as ArmorStand
	private val zombie: Zombie = Bukkit.getWorld("world").spawnEntity(owner.location, EntityType.ZOMBIE) as Zombie
	var summoned = true
	var level = 1
	var exp = 0L

	companion object {
		val pets = HashMap<UUID, UUID>()
		val followers = HashMap<UUID, UUID>()

		fun getExpToLevel(level: Int): Long? {
			return when (level) {
				10 -> null
				1 -> 100
				else -> (getExpToLevel(level - 1)!! * 2.5).toLong()
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

		Bukkit.getScheduler().scheduleSyncRepeatingTask(DungeonCrawler.instance, {
			if (summoned) {
				zombie.target = owner.asCraftPlayer()
				if (zombie.location.distance(owner.location) > 100) {
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
						print(type.getDamageTotal(level))
						if (type.baseDamage > 0)
							closestEntity!!.damage(type.getDamageTotal(level), owner)
						type.attack(this, closestEntity!!)
					}
				}
			} else {
				if (!zombie.isDead) {
					remove()
				}
			}
		}, type.attackSpeed, type.attackSpeed)

		Bukkit.getScheduler().scheduleSyncRepeatingTask(DungeonCrawler.instance, {
			if (summoned) {
				levelUp()
				entity.teleport(zombie.location.setDirection(owner.location.toVector().subtract(entity.location.toVector())).add(0.0, 2.0, 0.0), PlayerTeleportEvent.TeleportCause.PLUGIN)
			}
		}, 2L, 2L)
	}

	fun remove() {
		levelUp()
		saveLevels()

		pets.remove(entity.uniqueId)
		followers.remove(zombie.uniqueId)

		zombie.remove()
		entity.remove()
		summoned = false
	}

	fun saveLevels() {
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
		zombie.isBaby = true
		(zombie as CraftZombie).handle.b(true)
		zombie.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1, false, false))
		zombie.setMetadata("follower", FixedMetadataValue(DungeonCrawler.instance, owner.uniqueId.toString()))
		pets[zombie.uniqueId] = owner.uniqueId
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