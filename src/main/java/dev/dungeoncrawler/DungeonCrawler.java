package dev.dungeoncrawler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DungeonCrawler extends JavaPlugin {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
	}
}
