package dev.dungeoncrawler.data

import com.mongodb.*
import org.bson.Document

fun main() {
	DatabaseHandler()
}

class DatabaseHandler {
	private val host = "reeve.eastus2.cloudapp.azure.com"
	private val port = 27017

	private val client = MongoClient(ServerAddress(host, port), MongoCredential.createCredential("admin", "admin", "rmc1124Erin!".toCharArray()), MongoClientOptions.builder().build())
	private val database = client.getDatabase("test")
	private val players = database.getCollection("players")

	val id = "767676"

	init {
		if (hasPlayersDocument(id)) {
			System.out.println(getPlayersDocument(id)!!["name"])
		} else {
			val document = Document()
			document["id"] = id
			document["name"] = "test_object"
			document["value"] = 70000
			players.insertOne(document)
		}
	}

	fun getPlayersDocument(id: String): Document? {
		val query = BasicDBObject()
		query["id"] = id
		return players.find(query).cursor().tryNext()
	}

	fun hasPlayersDocument(id: String): Boolean {
		return getPlayersDocument(id) != null
	}

	private fun getQuery(id: String): BasicDBObject {
		val obj = BasicDBObject()
		obj["id"] = id
		return obj
	}

	fun insertOrUpdatePlayers(id: String, document: Document) {
		if (hasPlayersDocument(id)) {
			players.updateOne(getQuery(id), document)
		} else {
			players.insertOne(document)
		}
	}
}