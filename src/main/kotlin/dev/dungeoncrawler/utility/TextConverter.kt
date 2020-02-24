package dev.dungeoncrawler.utility

import org.bukkit.entity.Player


object TextConverter {
	fun convert(text: String?): String {
		if (text == null || text.length == 0) {
			return "\"\""
		}
		var c: Char
		var i: Int
		val len = text.length
		val sb = StringBuilder(len + 4)
		var t: String
		sb.append('"')
		i = 0
		while (i < len) {
			c = text[i]
			when (c) {
				'\\', '"' -> {
					sb.append('\\')
					sb.append(c)
				}
				'/' -> {
					sb.append('\\')
					sb.append(c)
				}
				'\b' -> sb.append("\\b")
				'\t' -> sb.append("\\t")
				'\n' -> sb.append("\\n")
				'\r' -> sb.append("\\r")
				else -> if (c < ' ') {
					t = "000" + Integer.toHexString(c.toInt())
					sb.append("\\u").append(t.substring(t.length - 4))
				} else {
					sb.append(c)
				}
			}
			i += 1
		}
		sb.append('"')
		return sb.toString()
	}
	
	fun setPlayerName(player: Player, text: String): String {
		return text.replace("(?i)\\{PLAYER\\}".toRegex(), player.name)
	}
}