import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins { id("com.github.johnrengelman.shadow") }

val fabric_loader_version: String by project
val fabric_api_version: String by project
val architectury_api_version: String by project
val forge_config_api_port_version: String by rootProject

architectury {
    platformSetupLoomIde()
    fabric()
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
    getByName("developmentFabric").extendsFrom(common)

    val shadowBundle =
        create("shadowBundle") {
            isCanBeResolved = true
            isCanBeConsumed = false
        }
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:$fabric_loader_version")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")
    modImplementation("dev.architectury:architectury-fabric:$architectury_api_version")
    "common"(project(":common", "namedElements")) { isTransitive = false }
    "shadowBundle"(project(":common", "transformProductionFabric"))
    modApi("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:$forge_config_api_port_version")
}

tasks.withType<ProcessResources> {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") { expand("version" to project.version) }
}

tasks.withType<ShadowJar> {
    configurations = listOf(project.configurations.getByName("shadowBundle"))
    archiveClassifier.set("dev-shadow")
}

tasks.remapJar { input.set(tasks.getByName<ShadowJar>("shadowJar").archiveFile) }
