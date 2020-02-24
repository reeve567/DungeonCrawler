package dev.dungeoncrawler.extensions

import net.minecraft.server.v1_8_R3.ChunkSection
import org.bukkit.Chunk
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk

fun Chunk.copyTo(to: Chunk) {
	val from = this
	val fromCC = from as CraftChunk
	val toCC = to as CraftChunk
	val fromChunk = from.handle
	val toChunk = to.handle
	
	val tsec: Array<ChunkSection?> = fromChunk.sections.clone()
	val asec = arrayOfNulls<ChunkSection>(tsec.size)
	
	for (n in tsec.indices) {
		if (tsec[n] != null) {
			asec[n] = ChunkSection(tsec[n]!!.yPosition, true, tsec[n]!!.idArray.clone())
			asec[n]!!.a(tsec[n]!!.emittedLightArray)
		} else {
			asec[n] = tsec[n]
		}
	}
	toChunk.a(asec)
	toChunk.removeEntities()
	world.refreshChunk(toChunk.bukkitChunk.x, toChunk.bukkitChunk.z)
	
}