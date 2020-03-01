package dev.dungeoncrawler.npcs

import dev.dungeoncrawler.data.PlayerDataManager
import dev.dungeoncrawler.utility.*
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.util.EulerAngle
import java.util.*
import kotlin.Comparator
import kotlin.math.PI

class Merchant(loc: Location, val playerDataManager: PlayerDataManager) : Listener {
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
	fun onClick(e: PlayerInteractAtEntityEvent) {
		if (e.rightClicked.uniqueId == entity.uniqueId) {
			e.isCancelled = true
			market(1, SortType.DATE_ADDED, e.player)
		}
		
	}
	
	fun market(page: Int, sortType: SortType, player: Player) {
		gui("Market") {
			var temp: List<Triple<Date, Pair<Int, Boolean>, ItemStack>> = emptyList()
			playerDataManager.marketItems.values.forEach {
				it.forEach { it1 ->
					if (it1.second.second)
						temp.toMutableList().add(it1)
				}
			}
			temp = temp.filter { it.second.second }
			
			temp.sortedWith(Comparator { o1, o2 ->
				when (sortType) {
					SortType.DATE_ADDED -> {
						when {
							o1.first.time > o2.first.time -> 1
							o1.first.time == o2.first.time -> 0
							else -> -1
						}
					}
					SortType.ENDING_SOON -> {
						when {
							o1.first.time > o2.first.time -> -1
							o1.first.time == o2.first.time -> 0
							else -> 1
						}
					}
					SortType.PRICE -> {
						when {
							o1.second.first < o2.second.first -> 1
							o1.second.first == o2.second.first -> 0
							else -> -1
						}
					}
				}
			})
			
			for (i in 0 until 45) {
				if (temp.size > i)
					clickableItem {
						itemStack = item(temp[i].third) {
							itemMeta {
								lore = listOf("§7Price: §6${temp[i].second.first}")
							}
						}
					}
			}
			
		}.open(player)
	}
	
	enum class SortType {
		DATE_ADDED,
		ENDING_SOON,
		PRICE
	}
	
}