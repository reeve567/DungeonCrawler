plugins {
	kotlin("jvm") version "1.3.61"
	id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "dev.dungeoncrawler"
version = "1.0-SNAPSHOT"



repositories {
	mavenCentral()
	mavenLocal()
	maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
	maven("https://oss.sonatype.org/content/repositories/snapshots")
	maven("https://jitpack.io")
}

dependencies {
	implementation(kotlin("stdlib"))
	implementation(kotlin("test-junit"))
	implementation("com.google.code.gson:gson:2.8.6")
	compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
	compileOnly("org.bukkit:craftbukkit:1.8.8-R0.1-SNAPSHOT")
	compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}

task("testAndJar") {
	dependsOn("test")
	dependsOn("shadowJar")
	tasks.findByName("shadowJar")!!.mustRunAfter("test")
}

tasks.register<Copy>("devin") {
	dependsOn("shadowJar")
	from(file("$buildDir/libs/DungeonCrawler-1.0-SNAPSHOT-all.jar"))
	destinationDir = file("C:/Users/User/Desktop/Minecraft/Bukkit/plugins")
}

configure<JavaPluginConvention> {
	sourceCompatibility = JavaVersion.VERSION_1_8
}
