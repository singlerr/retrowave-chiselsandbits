val enabled_platforms: String by project
val fabric_loader_version: String by project
val architectury_api_version: String by project
val cloth_config_api_version: String by rootProject

architectury { common(enabled_platforms.split(',')) }

loom { accessWidenerPath.set(file("src/main/resources/retrowave.accesswidener")) }

dependencies {
    modImplementation("net.fabricmc:fabric-loader:$fabric_loader_version")
    modImplementation("dev.architectury:architectury:$architectury_api_version")
    modApi("me.shedaniel.cloth:cloth-config:${cloth_config_api_version}")
}
