package dev.zornov.uploader.service.transfer

import dev.zornov.uploader.configuration.FileTransferConfiguration
import dev.zornov.uploader.core.LifecycleService

interface FileTransferService<T : FileTransferConfiguration> : LifecycleService {
    val config: T

    suspend fun upload(localPath: String, remotePath: String)
    suspend fun download(remotePath: String, localPath: String)
}