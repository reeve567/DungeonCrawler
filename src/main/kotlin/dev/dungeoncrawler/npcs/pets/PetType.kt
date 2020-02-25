package dev.dungeoncrawler.npcs.pets

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

enum class PetType(val baseDamage: Double, val attackSpeed: Long, val petName: String, val id: Int, val description: String, private val headValue: String) : Attacker {
	BEE(3.0, 20, "Bee", 1, "Just a humble bee.", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdkYjlhNjA0N2QyOTlhNjk0NWZhMzYwMjk5ZTEyYTEzNzM2ZDU2ZjFmZGZjMTkyZWMyMGYyOWNmNDY4MThjIn19fQ==") {
		override fun attack(pet: Pet, entity: LivingEntity) {
		}
	},
	WATER_ORB(10.0, 20, "Water Orb", 2, "... not really a pet", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTY3OTliZmFhM2EyYzYzYWQ4NWRkMzc4ZTY2ZDU3ZDlhOTdhM2Y4NmQwZDlmNjgzYzQ5ODYzMmY0ZjVjIn19fQ==") {
		override fun attack(pet: Pet, entity: LivingEntity) {
		}
	},
	LIGHTNING_ORB(55.0, 10, "Lightning Orb", 50, "... not really a pet.", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzNkMTQ1NjFiYmQwNjNmNzA0MjRhOGFmY2MzN2JmZTljNzQ1NjJlYTM2ZjdiZmEzZjIzMjA2ODMwYzY0ZmFmMSJ9fX0=") {
		override fun attack(pet: Pet, entity: LivingEntity) {
		}
	}
	;
	
	companion object {
		fun getById(id: Int): PetType? {
			return values().find { it.id == id }
		}
	}
	
	fun getDamageTotal(level: Int): Double {
		return ((baseDamage * level / if (level > 1) 1.5 else 1.0) * 100).toInt() / 100.0
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