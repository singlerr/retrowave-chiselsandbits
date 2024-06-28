import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins { id("com.github.johnrengelman.shadow") }

val neoforge_version: String by project
val architectury_api_version: String by project
val cloth_config_api_version: String by rootProject

architectury {
    platformSetupLoomIde()
    neoForge()
}

loom { accessWidenerPath.set(project(":common").loom.accessWidenerPath) }

configurations {
    val common =
        create("common") {
            isCanBeResolved = true
            isCanBeConsumed = false
        }
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    getByName("developmentNeoForge").extendsFrom(common)

    create("shadowBundle") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

repositories {
    maven {
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
}

dependencies {
    neoForge("net.neoforged:neoforge:$neoforge_version")
    modImplementation("dev.architectury:architectury-neoforge:$architectury_api_version")
    "common"(project(":common", "namedElements")) { isTransitive = false }
    "shadowBundle"(project(":common", "transformProductionNeoForge"))
    modApi("me.shedaniel.cloth:cloth-config-neoforge:${cloth_config_api_version}")
}

tasks.withType<ProcessResources> {
    inputs.property("version", project.version)

    filesMatching("META-INF/neoforge.mods.toml") { expand("version" to project.version) }
}

tasks.withType<ShadowJar> {
    configurations = listOf(project.configurations.getByName("shadowBundle"))
    archiveClassifier.set("dev-shadow")
}

tasks.remapJar { input.set(tasks.getByName<ShadowJar>("shadowJar").archiveFile) }
