package dev.zornov.uploader

import dev.zornov.uploader.plugin.UploaderExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class UploaderPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(
            "uploader",
            UploaderExtension::class.java,
            project
        )

        val uploadTask = project.tasks.register(
            "uploadJar",
            UploadJarTask::class.java
        ) { task ->
            task.uploaderExtension = extension
            task.group = "upload"
            task.description = "Uploads JAR via SFTP and executes via Pterodactyl or RCON"
        }

        project.afterEvaluate {
            if (extension.isEnabled) {
                project.tasks.getByName("build").finalizedBy(uploadTask)
            }
        }
    }
}