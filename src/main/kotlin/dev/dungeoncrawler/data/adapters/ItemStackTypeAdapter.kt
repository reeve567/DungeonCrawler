package dev.dungeoncrawler.data.adapters

import com.google.gson.*
import dev.dungeoncrawler.utility.item
import dev.dungeoncrawler.utility.itemMeta
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type

class ItemStackTypeAdapter : JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
	override fun serialize(src: ItemStack?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
		return Gson().toJsonTree(src?.serialize())
	}

	override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ItemStack {
		val obj = json?.asJsonObject ?: return ItemStack(Material.AIR)
		return item(Material.getMaterial(obj["type"].asString)) {
			obj["durability"]?.also {
				durability = it.asShort
			}
			obj["amount"]?.also {
				amount = it.asInt
			}
			obj["meta"]?.also {
				itemMeta {
					obj["displayName"]?.also {
						displayName = it.asString
					}
					obj["lore"]?.also {
						lore = it.asJsonArray.toList().map { it.asString }
					}
				}
				obj["enchantments"]?.also {
					val enchants = it.asJsonObject
					for (entry in enchants.entrySet()) {
						addUnsafeEnchantment(Enchantment.getByName(entry.key.substring(entry.key.indexOf(',') + 1, entry.key.indexOf(']')).replace(" ", "")), entry.value.asInt)
					}
				}

			}
		}
	}

}