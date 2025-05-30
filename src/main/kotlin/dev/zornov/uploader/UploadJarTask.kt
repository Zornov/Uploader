package dev.zornov.uploader

import dev.zornov.uploader.controller.pterodactyl.PterodactylControllerService
import dev.zornov.uploader.controller.rcon.RCONControllerService
import dev.zornov.uploader.file.sftp.SFTPFilesService
import dev.zornov.uploader.plugin.UploaderExtension
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class UploadJarTask : DefaultTask() {

    @Nested
    lateinit var uploaderExtension: UploaderExtension

    @TaskAction
    fun upload() = runBlocking {
        if (!uploaderExtension.isEnabled) return@runBlocking

        uploadViaSFTP()
        executeRconCommands()
        executePterodactylCommands()
    }

    suspend fun uploadViaSFTP() {
        with(uploaderExtension.sftpConfig) {
            @Suppress("DEPRECATION")
            val jar = File(project.buildDir, "libs/$fileName")
            SFTPFilesService(host, port, username, password).apply {
                start()
                put(jar.absolutePath, remote, overwrite = true)
            }
        }
    }

    suspend fun executeRconCommands() {
        uploaderExtension.rconConfig.takeIf { it.isEnabled }?.let {
            RCONControllerService(it.host, it.port, it.password).apply {
                start()
                for (command in it.commands.list) {
                    executeCommand(command)
                }
                stop()
            }
        }
    }

    suspend fun executePterodactylCommands() {
        uploaderExtension.pterodactylConfig.takeIf { it.isEnabled }?.let {
            PterodactylControllerService(it.apiUrl, it.apiKey, it.serverId).apply {
                start()
                for (command in it.commands.list) {
                    executeCommand(command)
                }
                stop()
            }
        }
    }
}
