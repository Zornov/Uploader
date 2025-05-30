package dev.zornov.uploader.file

interface FilesService {

    suspend fun start()
    suspend fun stop()

    suspend fun put(local: String, remote: String, overwrite: Boolean)
    suspend fun get(local: String, remote: String, overwrite: Boolean)
    suspend fun rename(remote: String, new: String)
    suspend fun remove(remote: String)
}