package dev.dungeoncrawler.data

import com.google.gson.*
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import net.minecraft.server.v1_8_R3.MojangsonParseException
import net.minecraft.server.v1_8_R3.MojangsonParser
import net.minecraft.server.v1_8_R3.NBTBase
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.io.IOException
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*

object GsonFactory {
	/*
    - I want to not use Bukkit parsing for most objects... it's kind of clunky
    - Instead... I want to start using any of Mojang's tags
    - They're really well documented + built into MC, and handled by them.
    - Rather than kill your old code, I'm going to write TypeAdapaters using Mojang's stuff.
     */
	private val g = Gson()
	private const val CLASS_KEY = "SERIAL-ADAPTER-CLASS-KEY"
	/**
	 * Returns a Gson instance for use anywhere with new line pretty printing
	 *
	 *
	 * Use @GsonIgnore in order to skip serialization and deserialization
	 *
	 * @return a Gson instance
	 */
	var prettyGson: Gson? = null
		get() {
			if (field == null) field = GsonBuilder().addSerializationExclusionStrategy(ExposeExlusion())
					.addDeserializationExclusionStrategy(ExposeExlusion())
					.registerTypeHierarchyAdapter(ItemStack::class.java, ItemStackGsonAdapter())
					.registerTypeAdapter(PotionEffect::class.java, PotionEffectGsonAdapter())
					.registerTypeAdapter(Location::class.java, LocationGsonAdapter())
					.registerTypeAdapter(Date::class.java, DateGsonAdapter())
					.setPrettyPrinting()
					.disableHtmlEscaping()
					.create()
			return field
		}
		private set
	/**
	 * Returns a Gson instance for use anywhere with one line strings
	 *
	 *
	 * Use @GsonIgnore in order to skip serialization and deserialization
	 *
	 * @return a Gson instance
	 */
	var compactGson: Gson? = null
		get() {
			if (field == null) field = GsonBuilder().addSerializationExclusionStrategy(ExposeExlusion())
					.addDeserializationExclusionStrategy(ExposeExlusion())
					.registerTypeHierarchyAdapter(ItemStack::class.java, ItemStackGsonAdapter())
					.registerTypeAdapter(PotionEffect::class.java, PotionEffectGsonAdapter())
					.registerTypeAdapter(Location::class.java, LocationGsonAdapter())
					.registerTypeAdapter(Date::class.java, DateGsonAdapter())
					.disableHtmlEscaping()
					.create()
			return field
		}
		private set

	/**
	 * Creates a new instance of Gson for use anywhere
	 *
	 *
	 * Use @GsonIgnore in order to skip serialization and deserialization
	 *
	 * @return a Gson instance
	 */
	fun getNewGson(prettyPrinting: Boolean): Gson {
		val builder = GsonBuilder().addSerializationExclusionStrategy(ExposeExlusion())
				.addDeserializationExclusionStrategy(ExposeExlusion())
				.registerTypeHierarchyAdapter(ItemStack::class.java, NewItemStackAdapter())
				.disableHtmlEscaping()
		if (prettyPrinting) builder.setPrettyPrinting()
		return builder.create()
	}

	private fun recursiveSerialization(o: ConfigurationSerializable): MutableMap<String, Any> {
		val originalMap = o.serialize()
		val map: MutableMap<String, Any> = HashMap()
		for ((key, o2) in originalMap) {
			if (o2 is ConfigurationSerializable) {
				val serializable = o2
				val newMap = recursiveSerialization(serializable)
				newMap[CLASS_KEY] = ConfigurationSerialization.getAlias(serializable.javaClass)
				map[key] = newMap
			}
		}
		map[CLASS_KEY] = ConfigurationSerialization.getAlias(o.javaClass)
		return map
	}

	private fun recursiveDoubleToInteger(originalMap: Map<String, Any?>?): Map<String, Any?> {
		val map: MutableMap<String, Any?> = HashMap()
		for ((key, o) in originalMap!!) {
			when (o) {
				is Double -> {
					val i = o.toInt()
					map[key] = i
				}
				is Map<*, *> -> {
					val subMap = o as Map<String, Any?>
					map[key] = recursiveDoubleToInteger(subMap)
				}
				else -> {
					map[key] = o
				}
			}
		}
		return map
	}

	private fun nbtToString(base: NBTBase): String {
		return base.toString().replace(",}", "}").replace(",]", "]").replace("[0-9]+\\:".toRegex(), "")
	}

	private fun removeSlot(item: ItemStack?): net.minecraft.server.v1_8_R3.ItemStack? {
		if (item == null) return null
		val nmsi = CraftItemStack.asNMSCopy(item) ?: return null
		val nbtt = nmsi.tag
		if (nbtt != null) {
			nbtt.remove("Slot")
			nmsi.tag = nbtt
		}
		return nmsi
	}

	private fun removeSlotNBT(item: ItemStack?): ItemStack? {
		if (item == null) return null
		val nmsi = CraftItemStack.asNMSCopy(item) ?: return null
		val nbtt = nmsi.tag
		if (nbtt != null) {
			nbtt.remove("Slot")
			nmsi.tag = nbtt
		}
		return CraftItemStack.asBukkitCopy(nmsi)
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(AnnotationTarget.FIELD)
	annotation class Ignore

	private class ExposeExlusion : ExclusionStrategy {
		override fun shouldSkipField(fieldAttributes: FieldAttributes): Boolean {
			val ignore = fieldAttributes.getAnnotation(Ignore::class.java)
			if (ignore != null) return true
			val expose = fieldAttributes.getAnnotation(Expose::class.java)
			return expose != null && (!expose.serialize || !expose.deserialize)
		}

		override fun shouldSkipClass(aClass: Class<*>?): Boolean {
			return false
		}
	}

	private class NewItemStackAdapter : TypeAdapter<ItemStack?>() {
		@Throws(IOException::class)
		override fun write(jsonWriter: JsonWriter, itemStack: ItemStack?) {
			if (itemStack == null) {
				jsonWriter.nullValue()
				return
			}
			val item = removeSlot(itemStack)
			if (item == null) {
				jsonWriter.nullValue()
				return
			}
			try {
				jsonWriter.beginObject()
				jsonWriter.name("type")
				jsonWriter.value(itemStack.type.toString()) //I hate using this - but
				jsonWriter.name("amount")
				jsonWriter.value(itemStack.amount.toLong())
				jsonWriter.name("data")
				jsonWriter.value(itemStack.durability.toLong())
				jsonWriter.name("tag")
				if (item.tag != null) {
					jsonWriter.value(nbtToString(item.tag))
				} else jsonWriter.value("")
				jsonWriter.endObject()
			} catch (ex: Exception) {
				ex.printStackTrace()
			}
		}

		@Throws(IOException::class)
		override fun read(jsonReader: JsonReader): ItemStack? {
			if (jsonReader.peek() == JsonToken.NULL) {
				return null
			}
			jsonReader.beginObject()
			jsonReader.nextName()
			val type = Material.getMaterial(jsonReader.nextString())
			jsonReader.nextName()
			val amount = jsonReader.nextInt()
			jsonReader.nextName()
			val data = jsonReader.nextInt()
			val item = net.minecraft.server.v1_8_R3.ItemStack(CraftMagicNumbers.getItem(type), amount, data)
			jsonReader.nextName()
			val next = jsonReader.nextString()
			if (next.startsWith("{")) {
				var compound: NBTTagCompound? = null
				try {
					compound = MojangsonParser.parse(ChatColor.translateAlternateColorCodes('&', next))
				} catch (e: MojangsonParseException) {
					e.printStackTrace()
				}
				item.tag = compound
			}
			jsonReader.endObject()
			return CraftItemStack.asBukkitCopy(item)
		}
	}

	private class ItemStackGsonAdapter : TypeAdapter<ItemStack?>() {
		@Throws(IOException::class)
		override fun write(jsonWriter: JsonWriter, itemStack: ItemStack?) {
			if (itemStack == null) {
				jsonWriter.nullValue()
				return
			}
			jsonWriter.value(getRaw(removeSlotNBT(itemStack)))
		}

		@Throws(IOException::class)
		override fun read(jsonReader: JsonReader): ItemStack? {
			if (jsonReader.peek() == JsonToken.NULL) {
				jsonReader.nextNull()
				return null
			}
			return fromRaw(jsonReader.nextString())
		}

		private fun getRaw(item: ItemStack?): String {
			val serial = item!!.serialize()
			if (serial["meta"] != null) {
				val itemMeta = item.itemMeta
				val originalMeta = itemMeta.serialize()
				val meta: MutableMap<String, Any> = HashMap()
				for ((key, value) in originalMeta) meta[key] = value
				var o: Any
				for ((key, value) in meta) {
					o = value
					if (o is ConfigurationSerializable) {
						val serialized: Map<String, Any> = recursiveSerialization(o)
						meta[key] = serialized
					}
				}
				serial["meta"] = meta
			}
			return g.toJson(serial)
		}

		private fun fromRaw(raw: String): ItemStack? {
			val keys = g.fromJson<MutableMap<String, Any?>>(raw, seriType)
			if (keys["amount"] != null) {
				val d = keys["amount"] as Double?
				val i = d!!.toInt()
				keys["amount"] = i
			}
			val item: ItemStack
			item = try {
				ItemStack.deserialize(keys)
			} catch (e: Exception) {
				return null
			}
			if (item == null) return null
			if (keys.containsKey("meta")) {
				var itemmeta = keys["meta"] as Map<String, Any?>?
				itemmeta = recursiveDoubleToInteger(itemmeta)
				val meta = ConfigurationSerialization.deserializeObject(itemmeta, ConfigurationSerialization.getClassByAlias("ItemMeta")) as ItemMeta
				item.itemMeta = meta
			}
			return item
		}

		companion object {
			private val seriType = object : TypeToken<Map<String?, Any?>?>() {}.type
		}
	}

	private class PotionEffectGsonAdapter : TypeAdapter<PotionEffect?>() {
		@Throws(IOException::class)
		override fun write(jsonWriter: JsonWriter, potionEffect: PotionEffect?) {
			if (potionEffect == null) {
				jsonWriter.nullValue()
				return
			}
			jsonWriter.value(getRaw(potionEffect))
		}

		@Throws(IOException::class)
		override fun read(jsonReader: JsonReader): PotionEffect? {
			if (jsonReader.peek() == JsonToken.NULL) {
				jsonReader.nextNull()
				return null
			}
			return fromRaw(jsonReader.nextString())
		}

		private fun getRaw(potion: PotionEffect): String {
			val serial = potion.serialize()
			return g.toJson(serial)
		}

		private fun fromRaw(raw: String): PotionEffect {
			val keys = g.fromJson<Map<String, Any>>(raw, seriType)
			return PotionEffect(PotionEffectType.getById((keys[TYPE] as Double?)!!.toInt()), (keys[DURATION] as Double?)!!.toInt(), (keys[AMPLIFIER] as Double?)!!.toInt(), (keys[AMBIENT] as Boolean?)!!)
		}

		companion object {
			private val seriType = object : TypeToken<Map<String?, Any?>?>() {}.type
			private const val TYPE = "effect"
			private const val DURATION = "duration"
			private const val AMPLIFIER = "amplifier"
			private const val AMBIENT = "ambient"
		}
	}

	private class LocationGsonAdapter : TypeAdapter<Location?>() {
		@Throws(IOException::class)
		override fun write(jsonWriter: JsonWriter, location: Location?) {
			if (location == null) {
				jsonWriter.nullValue()
				return
			}
			jsonWriter.value(getRaw(location))
		}

		@Throws(IOException::class)
		override fun read(jsonReader: JsonReader): Location? {
			if (jsonReader.peek() == JsonToken.NULL) {
				jsonReader.nextNull()
				return null
			}
			return fromRaw(jsonReader.nextString())
		}

		private fun getRaw(location: Location): String? {
			val serial: MutableMap<String?, Any?> = HashMap()
			serial[UUID] = location.world.uid.toString()
			serial[X] = location.x.toString()
			serial[Y] = location.y.toString()
			serial[Z] = location.z.toString()
			serial[YAW] = location.yaw.toString()
			serial[PITCH] = location.pitch.toString()
			return g.toJson(serial)
		}

		private fun fromRaw(raw: String?): Location {
			val keys = g.fromJson<Map<String?, Any?>?>(raw, seriType)
			val w = Bukkit.getWorld(java.util.UUID.fromString(keys!![UUID] as String?))
			return Location(w, (keys[X] as String).toDouble(), (keys[Y] as String).toDouble(), (keys[Z] as String).toDouble(), (keys[YAW] as String).toFloat(), (keys[PITCH] as String).toFloat())
		}

		companion object {
			private val seriType = object : TypeToken<Map<String?, Any?>?>() {}.type
			private val UUID: String? = "uuid"
			private val X: String? = "x"
			private val Y: String? = "y"
			private val Z: String? = "z"
			private val YAW: String? = "yaw"
			private val PITCH: String? = "pitch"
		}
	}

	private class DateGsonAdapter : TypeAdapter<Date?>() {
		@Throws(IOException::class)
		override fun write(jsonWriter: JsonWriter?, date: Date?) {
			if (date == null) {
				jsonWriter!!.nullValue()
				return
			}
			jsonWriter!!.value(date.time)
		}

		@Throws(IOException::class)
		override fun read(jsonReader: JsonReader?): Date? {
			if (jsonReader!!.peek() == JsonToken.NULL) {
				jsonReader.nextNull()
				return null
			}
			return Date(jsonReader.nextLong())
		}
	}
}