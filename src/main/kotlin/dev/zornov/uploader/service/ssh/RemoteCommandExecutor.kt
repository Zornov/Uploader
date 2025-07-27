package dev.zornov.uploader.service.ssh

import dev.zornov.uploader.core.LifecycleService

interface RemoteCommandExecutor : LifecycleService {
    suspend fun execute(command: String): String
}