package dev.dungeoncrawler.npcs

import dev.dungeoncrawler.utility.head
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.util.EulerAngle
import kotlin.math.PI

class Merchant(loc: Location) : Listener {
	val entity: ArmorStand
	init {
		/*
		Command:
		/summon ArmorStand ~ ~ ~ {Invulnerable:1b,NoBasePlate:1b,NoGravity:1b,ShowArms:1b,Pose:{Head:[7f,0f,0f],LeftArm:[305f,330f,0f],RightArm:[309f,30f,0f]}}
		 */
		
		val rad = PI / 180
		
		entity = loc.world.spawnEntity(loc, EntityType.ARMOR_STAND) as ArmorStand
		entity.leftArmPose = EulerAngle(305.0 * rad, 330.0 * rad, 0.0)
		entity.rightArmPose = EulerAngle(305.0 * rad, 30.0 * rad, 0.0)
		entity.headPose = EulerAngle(10.0 * rad, 0.0, 0.0)
		entity.setGravity(false)
		entity.setBasePlate(false)
		entity.setArms(true)
		
		val chestplate = ItemStack(Material.LEATHER_CHESTPLATE)
		var meta = chestplate.itemMeta as LeatherArmorMeta
		meta.color = Color.RED
		chestplate.itemMeta = meta
		
		entity.chestplate = chestplate
		
		val pants = ItemStack(Material.LEATHER_LEGGINGS)
		meta = pants.itemMeta as LeatherArmorMeta
		meta.color = Color.BLACK
		pants.itemMeta = meta
		
		entity.leggings = pants
		
		entity.helmet = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWJhOWQ0OTRmMTMzMmQ5N2Y4YzA1Y2Y1YTQ5M2M2NTEzODE0ZTM3Y2QyMzBmODBkOTI0MDExNzM5Yjc4Yzg1MCJ9fX0=")
	}
	
	@EventHandler
	fun onClick(e : PlayerInteractAtEntityEvent) {
		if (e.rightClicked.uniqueId == entity.uniqueId) {
			e.isCancelled = true
			
		}
		
	}
	
}