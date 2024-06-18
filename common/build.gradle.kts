val enabled_platforms:String by project
val fabric_loader_version:String by project
val architectury_api_version:String by project

architectury {
    common(enabled_platforms.split(','))
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:$fabric_loader_version")
    modImplementation("dev.architectury:architectury:$architectury_api_version")
}