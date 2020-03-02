package dev.dungeoncrawler.handler

import dev.dungeoncrawler.DungeonCrawler
import dev.dungeoncrawler.FoodData
import dev.dungeoncrawler.dungeon.Dungeon
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.min

class FoodHandler(dungeonCrawler : DungeonCrawler) : Listener {

    val eatCooldown : HashMap<UUID, Long> = HashMap()

    @EventHandler
    fun onConsume(e: PlayerInteractEvent) {
        if(e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {
            if(e.item != null) {
                val food : FoodData.Food? = FoodData.getFood(e.item.type.name)
                if (food != null) {
                    if(e.player.health < e.player.maxHealth) {
                        if (!eatCooldown.containsKey(e.player.uniqueId) || System.currentTimeMillis() - eatCooldown[e.player.uniqueId]!! >= 3000) {
                            e.player.health = min(e.player.health + food.heal, e.player.maxHealth)
                            if(e.player.itemInHand.amount > 1)
                                e.player.itemInHand.amount -= 1
                            else
                                e.player.itemInHand = null
                            e.player.playSound(e.player.location, Sound.EAT, 1.0f, 1.0f)
                            eatCooldown[e.player.uniqueId] = System.currentTimeMillis()
                        } else {
                            val timeLeft = 3 - (System.currentTimeMillis() - eatCooldown[e.player.uniqueId]!!) / 1000.0
                            e.player.sendMessage("§cYou cannot do that for another $timeLeft second(s).")
                        }
                    } else {
                        e.player.sendMessage("§cYou are on full health!")
                    }
                }
            }
        }
    }

    @EventHandler
    fun onFoodChange(e: FoodLevelChangeEvent) {
        e.foodLevel = 20
    }
}