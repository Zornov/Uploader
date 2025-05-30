package dev.zornov.uploader.controller

interface CommandExecuteService {
    suspend fun start()
    suspend fun stop()

    suspend fun executeCommand(command: String)
}