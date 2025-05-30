@file:Suppress("unused")

package dev.zornov.uploader.plugin

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested

open class UploaderExtension(project: Project) {

    @get:Input
    var isEnabled: Boolean = false

    @get:Nested val sftpConfig = SftpConfig(project)
    @get:Nested val pterodactylConfig = PterodactylConfig()
    @get:Nested val rconConfig = RconConfig()

    fun sftp(action: Action<SftpConfig>) = action.execute(sftpConfig)
    fun pterodactyl(action: Action<PterodactylConfig>) = action.execute(pterodactylConfig)
    fun rcon(action: Action<RconConfig>) = action.execute(rconConfig)
}

open class SftpConfig(project: Project) {
    @get:Input var host = "localhost"
    @get:Input var port = 22
    @get:Input var username = ""
    @get:Input var password = ""
    @get:Input var fileName = "${project.name}.jar"
    @get:Input var remote = "server.jar"
}

open class PterodactylConfig {
    @get:Input var isEnabled = false
    @get:Input var apiUrl = ""
    @get:Input var apiKey = ""
    @get:Input var serverId = ""

    @get:Nested val commands = CommandList()
    fun commands(action: Action<CommandList>) = action.execute(commands)
}

open class RconConfig {
    @get:Input var isEnabled = false
    @get:Input var host = "localhost"
    @get:Input var port = 25575
    @get:Input var password = ""

    @get:Nested val commands = CommandList()
    fun commands(action: Action<CommandList>) = action.execute(commands)
}

open class CommandList {
    @get:Input val list: MutableList<String> = mutableListOf()
    fun command(cmd: String) = list.add(cmd)
}