import net.fabricmc.loom.api.LoomGradleExtensionAPI
plugins {
    id("dev.architectury.loom") version "1.6-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    java
    base
}

val minecraft_version:String by project
val maven_group:String by project
val mod_version:String by project
val archives_name:String by project

architectury{
    minecraft = minecraft_version
}

allprojects {
    apply(plugin = "java")
    group = maven_group
    version = mod_version
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")
    apply(plugin = "java")
    apply(plugin = "base")

    base{
        archivesName = "$archives_name-$name"
    }
    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")
    repositories {}

    dependencies {
        "minecraft"("net.minecraft:minecraft:$minecraft_version")
        "mappings"(loom.officialMojangMappings())
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    java{
        withSourcesJar()
    }

}