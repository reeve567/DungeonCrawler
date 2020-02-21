package dev.dungeoncrawler.data

class BankData(var pages: HashMap<Int, BankPageData> = HashMap()) {
	init {
		if (pages.isEmpty()) {
			pages[1] = BankPageData()
		}
	}
}