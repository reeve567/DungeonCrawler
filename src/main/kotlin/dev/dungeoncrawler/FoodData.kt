package dev.dungeoncrawler

object FoodData {
    const val EAT_COOLDOWN = 3

    enum class Food(val heal : Double) {
        APPLE(1.0),
        BREAD(2.0),
        COOKIE(2.0);
    }

    fun getFood(name : String) : Food? {
        for(f in Food.values()) {
            if(f.name == name)
                return f
        }
        return null
    }
}