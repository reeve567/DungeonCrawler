package dev.dungeoncrawler.dungeon

import dev.dungeoncrawler.Constants
import dev.dungeoncrawler.extensions.isSafe
import dev.dungeoncrawler.utility.item
import dev.dungeoncrawler.utility.itemMeta
import org.bukkit.*
import org.bukkit.block.Chest
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.pow
import kotlin.random.Random
import kotlin.random.nextInt

class Floor(val dungeon: Dungeon, val number: Int) : Listener {

	val world: World = Bukkit.getWorld("world")
	private val rooms: ArrayList<Room> = ArrayList()
	private val visited = HashMap<UUID, HashSet<Pair<Int, Int>>>()
	private val left = HashMap<UUID, Int>()
	private val mobs = HashMap<UUID, UUID>()

	@EventHandler
	fun onChangeChunk(e: PlayerMoveEvent) {
		if (e.to.chunk != e.from.chunk) {
			val room = rooms.find { it.x == e.to.chunk.x && it.z == e.to.chunk.z }
			if (room != null) {
				val pair = Pair(e.to.chunk.x, e.to.chunk.z)
				val chunk = room.getChunk()
				fun addChest() {
					if (Random.nextInt(IntRange(1, 100)) <= Constants.CHEST_SPAWN_CHANCE) {
						var found = false
						do {
							val rand = getRandomSpawn()
							for (i in 10..14) {
								var block = chunk.getBlock(rand.first, i, rand.second)
								if (block.isSafe() && !found) {
									block = block.getRelative(0, 1, 0)
									found = true
									block.type = Material.CHEST
									val firework = block.location.world.spawnEntity(block.location.add(0.5, 1.5, 0.5), EntityType.FIREWORK) as Firework
									val meta = firework.fireworkMeta
									meta.addEffect(
											FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(Color.RED).withColor(Color.BLACK).build()
									)
									firework.fireworkMeta = meta
									Bukkit.getScheduler().scheduleSyncDelayedTask(dungeon.plugin, {
										firework.playEffect(EntityEffect.FIREWORK_EXPLODE)
									}, 1)

									val chest = block.state as Chest
									val lootCount = Random.nextInt(IntRange(Constants.CHEST_MIN_LOOT, Constants.CHEST_MAX_LOOT))
									for (i in 0 until lootCount) {
										val slot = Random.nextInt(0, chest.blockInventory.size)
										// set to some loot from a loot table or smth
										chest.blockInventory.setItem(slot, item(Material.DIAMOND) {
											itemMeta {
												displayName = "§6just some fucking diamond"
											}
										})
									}
								}
							}
						} while (!found)

					}
				}

				fun spawnMobs() {
					val amount = Random.nextInt(IntRange(Constants.MOB_SPAWN_MIN, Constants.MOB_SPAWN_MAX))
					for (i in 0 until amount) {
						var found = false
						do {
							val rand = getRandomSpawn()
							for (y in 10..14) {
								val block = chunk.getBlock(rand.first, y, rand.second)
								if (!found && block.isSafe()) {
									found = true
									val type = Random.nextInt(0..1)
									when (type) {
										1 -> {
											val zombie = block.world.spawn(block.location.add(0.5, 1.0, 0.5), Zombie::class.java)
											zombie.equipment.helmet = ItemStack(Material.STONE_BUTTON)
											zombie.equipment.helmetDropChance = 0f
											zombie.target = e.player
											mobs[zombie.uniqueId] = e.player.uniqueId
										}
										else -> {
											val skeleton = block.world.spawn(block.location.add(0.5, 1.0, 0.5), Skeleton::class.java)
											skeleton.equipment.helmet = ItemStack(Material.STONE_BUTTON)
											skeleton.equipment.helmetDropChance = 0f
											skeleton.target = e.player
											mobs[skeleton.uniqueId] = e.player.uniqueId
										}
									}
								}
							}
						} while (!found)
					}
					left[e.player.uniqueId] = amount
					room.createFakeDoors(e.player)
				}

				if (pair != Pair(0, 0)) {
					if (visited.containsKey(e.player.uniqueId)) {
						if (!visited[e.player.uniqueId]!!.contains(pair)) {
							addChest()
							spawnMobs()
							visited[e.player.uniqueId]!!.add(pair)
						}
					} else {
						addChest()
						spawnMobs()
						visited[e.player.uniqueId] = HashSet(setOf(pair))
					}
				}
			}
		}
	}
	
	@EventHandler
	fun chestCloseEvent(e: InventoryCloseEvent) {
		if (e.inventory.type == InventoryType.CHEST) {
			if (e.inventory.contents.find { it != null && it.type != Material.AIR } == null) {
				val chest = (e.inventory.holder as Chest).block
				if (roomExists(chest.chunk.x, chest.chunk.z)) {
					chest.type = Material.AIR
					(e.player as Player).playSound(chest.location, Sound.CLICK, 0.5f, 1f)
				}
			}
		}
	}

	@EventHandler
	fun onMobDeath(e: EntityDeathEvent) {
		if (e.entity.type == EntityType.SKELETON || e.entity.type == EntityType.ZOMBIE) {
			e.droppedExp = 0
			val killer = e.entity.killer
			if (roomExists(killer.location.chunk.x, killer.location.chunk.z)) {
				getRoom(killer.location.chunk.x, killer.location.chunk.z)?.also { room ->
					if (mobs.containsKey(e.entity.uniqueId)) {
						left[mobs[e.entity.uniqueId]!!] = left[mobs[e.entity.uniqueId]!!]!! - 1
						if (left[mobs[e.entity.uniqueId]!!]!! == 0) {
							room.createFakeDoors(Bukkit.getPlayer(mobs[e.entity.uniqueId]!!), Material.AIR, 0)
						}
						mobs.remove(e.entity.uniqueId)
					}
				}

				dungeon.playerDataManager.addBalance(killer, (Random.nextInt(6..12) * number.toDouble().pow(2.0)).toInt())
			}
		}
	}

	fun destroy() {
		for (room in rooms) {
			room.destroy()
		}
		rooms.clear()
	}

	// devote themselves to a direction and make most chance go to straight
	// maybe pass on chance value and make it smaller if it's going in another direction from straight

	// generate in squares
	// first square of 8 - generate 6
	// second square of 18 - generate 10 & keep generating if not next to one in first square yet
	// etc

	fun createRooms(rings: Int) {
		val rooms = HashMap<Pair<Int, Int>, Room>()
		rooms[Pair(0, 0)] = createRoom(0, 0, 0)
		fun create(roomsSize: Int, squareSize: Int) {
			if (rooms.size != 1)
				rooms.clear()
			while (rooms.size < roomsSize) {
				val x = Random.nextInt(IntRange(-squareSize, squareSize))
				val z = Random.nextInt(IntRange(-squareSize, squareSize))
				if (x == -squareSize || x == squareSize) {
					if (!rooms.containsKey(Pair(x, z)))
						rooms[Pair(x, z)] = createRoom(x, z, roomsSize * 5)

				} else if (z == -squareSize || z == squareSize) {
					if (!rooms.containsKey(Pair(x, z)))
						rooms[Pair(x, z)] = createRoom(x, z, roomsSize * 5)
				}
			}
			for (roomEntry in rooms) {
				this.rooms.add(roomEntry.value)
			}
		}

		for (i in 1..rings) {
			create((i * 4) + 2 + (if (i == 1) 1 else 0) + (if (i >= 4) 2 else 0), i)
		}

		for (room in this.rooms) {
			room.createDoors()
		}
	}

	fun createRoom(x: Int, z: Int, delay: Int): Room {
		val pfbIndex: Int = (Math.random() * (Constants.PREFAB_SIZE * Constants.PREFAB_SIZE)).toInt()
		val room = Room(this, x, z, pfbIndex)
		room.create(delay)
		return room
	}

	fun roomExists(x: Int, z: Int): Boolean {
		for (room in rooms) {
			if (room.x == x && room.z == z)
				return true
		}
		return false
	}

	private fun getRoom(x: Int, z: Int): Room? {
		return rooms.find { it.x == x && it.z == z }
	}

	fun teleportPlayer(player: Player) {
		var found = false
		val room = getRoom(0, 0)!!
		val chunk = room.getChunk()
		do {
			val rand = getRandomSpawn()
			for (y in 10..14) {
				val block = chunk.getBlock(rand.first, y, rand.second)
				if (!found && block.isSafe()) {
					player.teleport(block.location.add(0.5, 1.0, 0.5))
					found = true
				}
			}
		} while (!found)
	}

	private fun getRandomSpawn(): Pair<Int, Int> {
		return Pair(Random.nextInt(1..15), Random.nextInt(1..15))
	}

	fun roomExistsAroundWithin(x: Int, z: Int, x2: Int, z2: Int): Boolean {
		fun roomCheck(x: Int, z: Int): Boolean {
			if (roomExists(x, z)) {
				if (x <= x2 && x >= -x2) {
					if (z <= z2 && z >= -z2) {
						return true
					}
				}
			}
			return false
		}

		if (roomCheck(x + 1, z)) return true
		if (roomCheck(x - 1, z)) return true
		if (roomCheck(x, z + 1)) return true
		if (roomCheck(x, z - 1)) return true
		return false
	}
}