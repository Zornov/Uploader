package dev.zornov.uploader.controller.pterodactyl

import dev.zornov.uploader.controller.CommandExecuteService
import dev.zornov.uploader.ext.recordTiming
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

class PterodactylControllerService(
    val panelUrl: String,
    val apiKey: String,
    val serverId: String
) : CommandExecuteService {

    val logger = LoggerFactory.getLogger(javaClass)

    lateinit var client: HttpClient

    override suspend fun start() {
        logger.recordTiming(Level.INFO, "Initializing Pterodactyl API client") {
            client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }
        }
    }

    override suspend fun stop() {
        logger.recordTiming(Level.INFO, "Closing Pterodactyl API client") {
            if (::client.isInitialized) {
                runCatching { client.close() }
                    .onFailure { logger.warn("Failed to close HTTP client", it) }
            }
        }
    }

    override suspend fun executeCommand(command: String) {
        logger.recordTiming(Level.INFO, "Executing command '$command'") {
            when (command.lowercase()) {
                "start", "stop", "restart", "kill" -> {
                    sendSignal(command.lowercase())
                    "Signal '$command' sent successfully."
                }
                else -> {
                    val msg = "Unknown command '$command'"
                    logger.warn(msg)
                    msg
                }
            }
        }
    }


    suspend fun sendSignal(signal: String) {
        val endpoint = "$panelUrl/api/client/servers/$serverId/power"

        runCatching {
            client.post(endpoint) {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                header(HttpHeaders.Accept, "application/json")
                contentType(ContentType.Application.Json)
                setBody(mapOf("signal" to signal))
            }
        }.onFailure {
            logger.warn("Failed to send signal '$signal'", it)
        }
    }
}
