package dev.zornov.uploader.service.transfer.impl

import dev.zornov.uploader.configuration.FileTransferConfiguration
import dev.zornov.uploader.service.transfer.FileTransferService

class FtpTransferService(
    override val config: FileTransferConfiguration.Ftp
) : FileTransferService<FileTransferConfiguration.Ftp> {
    override suspend fun upload(localPath: String, remotePath: String) {

    }

    override suspend fun download(remotePath: String, localPath: String) {

    }
}
