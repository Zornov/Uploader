package dev.zornov.uploader

import dev.zornov.uploader.task.UploadAndRunTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class UploaderPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create("uploader", UploaderExtension::class.java)

        project.tasks.register("uploadAndRun", UploadAndRunTask::class.java) {
            it.extension.set(ext)
        }
    }
}