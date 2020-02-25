package dev.dungeoncrawler.data.adapters

import com.google.gson.*
import dev.dungeoncrawler.data.PetLevelData
import java.lang.reflect.Type

class PetLevelDataTypeAdapter : JsonSerializer<PetLevelData>, JsonDeserializer<PetLevelData> {
	override fun serialize(src: PetLevelData?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
		val obj = JsonObject()
		val idsArray = JsonArray()
		val levelsArray = JsonArray()
		val expsArray = JsonArray()

		if (src == null) {
			obj.add("ids", idsArray)
			obj.add("levels", levelsArray)
			obj.add("exps", levelsArray)
			return obj
		}
		src.levels.keys.forEach {
			idsArray.add(JsonPrimitive(it))
		}
		src.levels.values.forEach {
			levelsArray.add(JsonPrimitive(it.first))
			expsArray.add(JsonPrimitive(it.second))
		}
		
		obj.add("ids", idsArray)
		obj.add("levels", levelsArray)
		obj.add("exps", expsArray)
		return obj
	}
	
	override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PetLevelData {
		val petLevelData = PetLevelData()
		val ids = json.asJsonObject.getAsJsonArray("ids").map { it.asInt }
		val levels = json.asJsonObject.getAsJsonArray("levels").map { it.asInt }
		val exps = json.asJsonObject.getAsJsonArray("exps").map { it.asLong }
		for (i in ids.indices) {
			val id = ids[i]
			val level = levels[i]
			val exp = exps[i]
			petLevelData.levels[id] = level to exp
		}
		return petLevelData
	}
}