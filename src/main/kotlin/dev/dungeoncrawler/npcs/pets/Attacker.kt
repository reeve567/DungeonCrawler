package dev.dungeoncrawler.npcs.pets

import org.bukkit.entity.LivingEntity

interface Attacker {
	fun attack(pet: Pet, entity: LivingEntity)
}