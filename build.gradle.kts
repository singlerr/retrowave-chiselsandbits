import com.diffplug.gradle.spotless.SpotlessExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("dev.architectury.loom") version "1.6-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("com.diffplug.spotless") version "6.25.0" apply false
    id("io.freefair.lombok") version "8.2.1" apply false
    java
    base
}

val minecraft_version: String by project
val maven_group: String by project
val mod_version: String by project
val archives_name: String by project

architectury { minecraft = minecraft_version }

allprojects {
    apply(plugin = "com.diffplug.spotless")

    group = maven_group
    version = mod_version

    configure<SpotlessExtension> {
        java {
            target("src/*/java/**/*.java", "*/src/*/java/**/*.java")
            palantirJavaFormat()
            removeUnusedImports()
        }

        kotlinGradle {
            target(
                "*.gradle.kts",
                "*.settings.gradle.kts",
                "*/*.gradle.kts",
                "*/*.settings.gradle.kts"
            )
            ktfmt().kotlinlangStyle()
        }
    }
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")
    apply(plugin = "java")
    apply(plugin = "base")
    apply(plugin = "io.freefair.lombok")
    base { archivesName = "$archives_name-$name" }

    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")

    repositories {
        mavenCentral()
        maven {
            name = "Fuzs Mod Resources"
            url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
        }
        maven {
            name = "Terraformers"
            url = uri("https://maven.terraformersmc.com/")
        }
    }

    dependencies {
        "minecraft"("net.minecraft:minecraft:$minecraft_version")
        "mappings"(loom.officialMojangMappings())
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    java { withSourcesJar() }
}
