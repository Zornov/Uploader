package dev.zornov.uploader.service.ssh.impl

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import dev.zornov.uploader.configuration.SshConfiguration
import dev.zornov.uploader.service.ssh.RemoteCommandExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class SshCommandExecutor(
    val config: SshConfiguration
) : RemoteCommandExecutor {

    var session: Session? = null

    override fun start() {
        if (session?.isConnected == true) return

        val jsch = JSch()

        config.privateKeyPath?.let { keyPath ->
            if (keyPath.isNotEmpty()) {
                jsch.addIdentity(keyPath, config.password)
            }
        }

        session = jsch.getSession(config.username, config.host, config.port).apply {
            setConfig("StrictHostKeyChecking", "no")
            connect()
        }
    }

    override fun stop() {
        session?.disconnect()
        session = null
    }

    override suspend fun execute(command: String): String = withContext(Dispatchers.IO) {
        val channel = session?.openChannel("exec") as? ChannelExec
            ?: throw IllegalStateException("SSH session is not connected")

        val errorOutput = ByteArrayOutputStream()

        channel.setCommand(command)
        channel.setErrStream(errorOutput)
        channel.connect()

        val inputStream = channel.inputStream

        val result = inputStream.bufferedReader().use { it.readText() }

        while (!channel.isClosed) {
            Thread.sleep(100)
        }

        val exitStatus = channel.exitStatus
        channel.disconnect()

        val stderr = errorOutput.toString().trim()

        if (exitStatus != 0) {
            throw RuntimeException("Command failed with exit code $exitStatus:\n$stderr")
        }

        result.trim()
    }
}
