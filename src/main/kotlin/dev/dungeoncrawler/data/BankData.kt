package dev.dungeoncrawler.data

class BankData(private val pagesSerialized: Map<String, Map<String, Map<String, Any>>>? = null) {

	var pages: HashMap<Int, BankPageData> = HashMap()

	init {
		if (pagesSerialized != null) {
			for (entry in pagesSerialized) {
				pages[Integer.valueOf(entry.key)] = BankPageData(entry.value)
			}
		} else {
			pages[1] = BankPageData()
		}

		pagesSerialized?.also {

		}
	}

	fun serialize(): HashMap<String, HashMap<String, Map<String, Any>>> {
		val hash = HashMap<String, HashMap<String, Map<String, Any>>>()
		for (entry in pages) {
			hash[entry.key.toString()] = entry.value.serialize()
		}
		return hash
	}
}