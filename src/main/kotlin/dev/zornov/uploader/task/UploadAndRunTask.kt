package dev.zornov.uploader.task

import dev.zornov.uploader.UploaderExtension
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class UploadAndRunTask : DefaultTask() {
    @Internal
    val extension: Property<UploaderExtension> = project.objects.property(UploaderExtension::class.java)

    @TaskAction
    fun run() {
        extension.get().execute()
    }
}