package dev.zornov.uploader

import dev.zornov.uploader.configuration.FileTransferConfiguration
import dev.zornov.uploader.configuration.SshConfiguration
import dev.zornov.uploader.dsl.ActionContext
import dev.zornov.uploader.service.ssh.impl.SshCommandExecutor
import dev.zornov.uploader.service.transfer.impl.SftpTransferService
import kotlinx.coroutines.runBlocking

open class UploaderExtension {
    private var sftpConfig: FileTransferConfiguration.Sftp? = null
    private var sshConfig: SshConfiguration? = null
    private var actionBlock: (suspend ActionContext.() -> Unit)? = null

    fun sftp(block: FileTransferConfiguration.Sftp.() -> Unit) {
        val config = FileTransferConfiguration.Sftp().apply(block)
        sftpConfig = config
    }

    fun ssh(block: SshConfiguration.() -> Unit) {
        val config = SshConfiguration().apply(block)
        sshConfig = config
    }

    fun action(block: suspend ActionContext.() -> Unit) {
        actionBlock = block
    }

    internal fun execute() = runBlocking {
        val sftpConf = sftpConfig ?: error("SFTP configuration is required")
        val sshConf = sshConfig ?: error("SSH configuration is required")

        val sftp = SftpTransferService(sftpConf)
        val ssh = SshCommandExecutor(sshConf)

        sftp.start()
        ssh.start()

        val context = ActionContext(sftp, ssh)
        actionBlock?.invoke(context)

        ssh.stop()
        sftp.stop()
    }
}