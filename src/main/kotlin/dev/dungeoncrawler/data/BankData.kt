package dev.dungeoncrawler.data

class BankData(private val pagesSerialized: HashMap<Int, HashMap<Int, Map<String, Any>>>? = null) {

	var pages: HashMap<Int, BankPageData> = HashMap()

	init {
		if (pagesSerialized != null) {
			for (entry in pagesSerialized) {
				pages[entry.key] = BankPageData(entry.value)
			}
		} else {
			pages[1] = BankPageData()
		}

		pagesSerialized?.also {

		}
	}

	fun serialize(): HashMap<Int, HashMap<Int, Map<String, Any>>> {
		val hash = HashMap<Int, HashMap<Int, Map<String, Any>>>()
		for (entry in pages) {
			hash[entry.key] = entry.value.serialize()
		}
		return hash
	}
}