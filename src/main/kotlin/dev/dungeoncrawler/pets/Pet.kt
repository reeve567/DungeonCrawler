package dev.dungeoncrawler.pets

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import dev.dungeoncrawler.DungeonCrawler
import dev.dungeoncrawler.data.PlayerData
import dev.dungeoncrawler.extensions.asCraftPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftZombie
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

class Pet(val owner: Player, val type: Type, var playerData: PlayerData) : Listener {
	val entity: ArmorStand = Bukkit.getWorld("world").spawnEntity(owner.location, EntityType.ARMOR_STAND) as ArmorStand
	private val zombie: Zombie = Bukkit.getWorld("world").spawnEntity(owner.location, EntityType.ZOMBIE) as Zombie
	var summoned = true
	private var level = 1
	private var exp = 0
	
	companion object {
		fun getExpToLevel(level: Int): Int {
			if (level == 1) return 100
			else return (getExpToLevel(level - 1) * 2.5).toInt()
		}
	}
	
	init {
		Bukkit.getPluginManager().registerEvents(this, DungeonCrawler.instance)
		if (playerData.petLevels.levels.containsKey(type.name)) {
			level = playerData.petLevels.levels[type.name]!!.first
			exp = playerData.petLevels.levels[type.name]!!.second
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
					zombie.remove()
					entity.remove()
					summoned = false
					
					saveLevels()
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
					zombie.remove()
					entity.remove()
				}
			}
		}, type.attackSpeed, type.attackSpeed)
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(DungeonCrawler.instance, {
			if (summoned) {
				levelUp()
				entity.teleport(zombie.location.setDirection(owner.location.toVector().subtract(entity.location.toVector())).add(0.0, 1.5, 0.0), PlayerTeleportEvent.TeleportCause.PLUGIN)
			}
		}, 2L, 2L)
	}
	
	 fun saveLevels() {
		playerData.petLevels.levels[type.name] = level to exp
	}
	
	private fun levelUp() {
		if (exp > getExpToLevel(level)) {
			exp = 0
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
	}
	
	private fun setFollowerEntity() {
		zombie.isBaby = true
		(zombie as CraftZombie).handle.b(true)
		zombie.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1, false, false))
		zombie.setMetadata("follower", FixedMetadataValue(DungeonCrawler.instance, owner.uniqueId.toString()))
	}
	
	enum class Type(val baseDamage: Double, val attackSpeed: Long, val petName: String, val description: String, private val headValue: String) : Attacker {
		BEE(3.0, 20, "Bee", "Just a humble bee.", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdkYjlhNjA0N2QyOTlhNjk0NWZhMzYwMjk5ZTEyYTEzNzM2ZDU2ZjFmZGZjMTkyZWMyMGYyOWNmNDY4MThjIn19fQ==") {
			override fun attack(pet: Pet, entity: LivingEntity) {
			}
		},
		LIGHTNING_ORB(55.0, 10, "Lightning Orb", "... not really a pet.", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzNkMTQ1NjFiYmQwNjNmNzA0MjRhOGFmY2MzN2JmZTljNzQ1NjJlYTM2ZjdiZmEzZjIzMjA2ODMwYzY0ZmFmMSJ9fX0=") {
			override fun attack(pet: Pet, entity: LivingEntity) {
			}
		}
		;
		
		fun getDamageTotal(level: Int): Double {
			return ((baseDamage * level / if (level > 1) 1.5 else 1.0)*100).toInt() / 100.0
		}
		
		fun getHead(): ItemStack {
			val head = ItemStack(Material.SKULL_ITEM, 1, 3)
			val meta = head.itemMeta as SkullMeta
			
			val profile = GameProfile(UUID.randomUUID(), "")
			profile.properties.put("textures", Property("textures", headValue))
			
			val profileField = meta.javaClass.getDeclaredField("profile")
			profileField.isAccessible = true
			profileField.set(meta, profile)
			
			head.itemMeta = meta
			return head
		}
	}
	
	interface Attacker {
		fun attack(pet: Pet, entity: LivingEntity)
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	fun onDamage(e: EntityDamageEvent) {
		if (e.entity.hasMetadata("pet") || e.entity.hasMetadata("follower"))
			e.isCancelled = true
	}
	
	@EventHandler
	fun onPlayerDamaged(e: EntityDamageByEntityEvent) {
		if (e.damager.hasMetadata("follower"))
			e.isCancelled = true
	}
	
	
}