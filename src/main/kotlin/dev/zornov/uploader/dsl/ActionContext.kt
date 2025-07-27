package dev.zornov.uploader.dsl

import dev.zornov.uploader.configuration.FileTransferConfiguration
import dev.zornov.uploader.service.ssh.RemoteCommandExecutor
import dev.zornov.uploader.service.transfer.FileTransferService

class ActionContext(
    val sftp: FileTransferService<FileTransferConfiguration.Sftp>,
    val ssh: RemoteCommandExecutor
)