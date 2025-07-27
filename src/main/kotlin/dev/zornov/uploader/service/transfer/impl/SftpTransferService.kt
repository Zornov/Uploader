
package dev.zornov.uploader.service.transfer.impl

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import dev.zornov.uploader.configuration.FileTransferConfiguration
import dev.zornov.uploader.service.transfer.FileTransferService
import java.io.File

class SftpTransferService(
    override val config: FileTransferConfiguration.Sftp
) : FileTransferService<FileTransferConfiguration.Sftp> {
    var session: Session? = null
    var channelSftp: ChannelSftp? = null

    override fun start() {
        if (session?.isConnected == true && channelSftp?.isConnected == true) {
            return
        }

        val jsch = JSch()
        config.privateKeyPath?.let { keyPath ->
            jsch.addIdentity(keyPath, config.password)
        }

        session = jsch.getSession(config.username, config.host, config.port).apply {
            if (config.privateKeyPath == null) {
                setPassword(config.password)
            }
            setConfig("StrictHostKeyChecking", "no")
            connect()
        }

        channelSftp = (session!!.openChannel("sftp") as ChannelSftp).apply {
            connect()
        }
    }

    override fun stop() {
        channelSftp?.disconnect()
        session?.disconnect()
        channelSftp = null
        session = null
    }

    override suspend fun upload(localPath: String, remotePath: String) {
        val remoteFile = remotePath.substringBeforeLast('/', "")
        if (remoteFile.isNotEmpty()) {
            try {
                channelSftp?.mkdir(remoteFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        channelSftp?.put(localPath, remotePath)
    }

    override suspend fun download(remotePath: String, localPath: String) {
        val localFile = File(localPath)
        localFile.parentFile?.mkdirs()

        channelSftp?.get(remotePath, localPath)
    }
}