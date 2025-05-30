package dev.zornov.uploader.file.sftp

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import dev.zornov.uploader.ext.recordTiming
import dev.zornov.uploader.file.FilesService
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.io.File
import java.nio.file.FileAlreadyExistsException

class SFTPFilesService(
    val host: String,
    val port: Int,
    val username: String,
    val password: String
) : FilesService {
    val logger = LoggerFactory.getLogger(javaClass)

    lateinit var session: Session
    lateinit var sftp: ChannelSftp


    override suspend fun start() {
        logger.recordTiming(Level.INFO, "Connecting to SFTP server") {
            session = JSch()
                .getSession(username, host, port)
                .apply {
                    setConfig("StrictHostKeyChecking", "no")
                    setPassword(password)
                    connect()
                }

            sftp = (session.openChannel("sftp") as ChannelSftp)
                .apply { connect() }
        }
    }

    override suspend fun stop() {
        logger.recordTiming(Level.INFO, "Disconnecting from SFTP server") {
            if (::sftp.isInitialized) runCatching {
                if (sftp.isConnected) sftp.disconnect()
            }.onFailure { logger.warn("Failed to disconnect SFTP channel", it) }

            if (::session.isInitialized) runCatching {
                if (session.isConnected) session.disconnect()
            }.onFailure { logger.warn("Failed to disconnect SSH session", it) }
        }
    }

    override suspend fun put(local: String, remote: String, overwrite: Boolean) {
        logger.recordTiming(Level.INFO, "Uploading '$local' → '$remote' (overwrite=$overwrite)") {
            if (!overwrite) {
                runCatching { sftp.stat(remote) }
                    .onSuccess { throw FileAlreadyExistsException(remote) }
            }
            sftp.put(local, remote, ChannelSftp.OVERWRITE)
        }
    }

    override suspend fun get(local: String, remote: String, overwrite: Boolean) {
        logger.recordTiming(Level.INFO, "Downloading '$remote' → '$local' (overwrite=$overwrite)") {
            if (!overwrite && File(local).exists()) {
                throw FileAlreadyExistsException(local)
            }
            sftp.get(remote, local)
        }
    }

    override suspend fun rename(remote: String, new: String) {
        logger.recordTiming(Level.INFO, "Renaming '$remote' → '$new'") {
            runCatching { sftp.stat(new) }
                .onSuccess { throw FileAlreadyExistsException(new) }

            sftp.rename(remote, new)
        }
    }

    override suspend fun remove(remote: String) {
        logger.recordTiming(Level.INFO, "Removing '$remote'") {
            sftp.rm(remote)
        }
    }
}