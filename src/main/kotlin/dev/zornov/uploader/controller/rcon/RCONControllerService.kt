package dev.zornov.uploader.controller.rcon

import dev.zornov.uploader.controller.CommandExecuteService
import org.slf4j.LoggerFactory
import java.io.DataInputStream
import java.net.ConnectException
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicInteger

class RCONControllerService(
    val host: String,
    val port: Int,
    val password: String
) : CommandExecuteService {
    val logger = LoggerFactory.getLogger(javaClass)

    lateinit var socket: Socket
    val outputStream get() = socket.getOutputStream()
    val inputStream get() = socket.getInputStream()
    val dataInputStream get() = DataInputStream(inputStream)
    val requestIdCounter: AtomicInteger = AtomicInteger(1)

    override suspend fun start() {
        logger.info("Connecting to RCON server $host:$port")
        try {
            socket = Socket(host, port)
        } catch (e: ConnectException) {
            logger.error("Failed to connect to RCON server $host:$port")
            throw e
        }
        val response = send(RCONPacket.RequestType.LOGIN, password.toByteArray())
        check(response.requestId == response.responseId) { "RCON Login failed" }
        logger.info("Successfully connected to RCON server")
    }

    override suspend fun stop() {
        logger.info("Disconnecting from RCON server $host:$port")
        socket.close()
    }

    override suspend fun executeCommand(command: String) {
        logger.info("Executing command: $command")
        val response = send(RCONPacket.RequestType.COMMAND, command.toByteArray())
        logger.info("Response: ${String(response.payload)}")
    }

    fun send(type: RCONPacket.RequestType, payload: ByteArray): RCONPacket {
        val requestSize = 4 + 4 + payload.size + 2
        val requestId = requestIdCounter.getAndIncrement()
        val buffer = ByteBuffer.allocate(4 + requestSize).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            putInt(requestSize)
            putInt(requestId)
            putInt(type.id)
            put(payload)
            put(0)
            put(0)
        }
        synchronized(socket) {
            outputStream.write(buffer.array())
            val header = ByteArray(3 * 4)
            dataInputStream.readFully(header)
            val headerBuf = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)
            val size = headerBuf.int
            val responseId = headerBuf.int
            val responseType = RCONPacket.RequestType.getById(headerBuf.int)
            val payloadBytes = ByteArray(size - 4 - 4 - 2)
            dataInputStream.readFully(payloadBytes)
            dataInputStream.readFully(ByteArray(2))
            return RCONPacket(size, requestId, responseId, responseType, payloadBytes)
        }
    }
}