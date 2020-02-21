package dev.dungeoncrawler

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class DungeonCrawler : JavaPlugin() {
	override fun onEnable() {
		Bukkit.getPluginManager().registerEvents(JoinListener(), this)
	}
}