package dev.dungeoncrawler.data

import org.bukkit.inventory.ItemStack
import java.util.*

class MarketData(var items: ArrayList<Triple<Date, Pair<Double, Boolean>, ItemStack>>? = null)