package dev.dungeoncrawler

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
			document.put("id", id)
			document.put("name", "test_object")
			document.put("value", 70000)
			players.insertOne(document)
		}
	}

	fun getPlayersDocument(id: String): Document? {
		val query = BasicDBObject()
		query.put("id", id)
		return players.find(query).cursor().tryNext()
	}

	fun hasPlayersDocument(id: String): Boolean {
		return getPlayersDocument(id) != null
	}
}