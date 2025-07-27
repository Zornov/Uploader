package dev.zornov.uploader.configuration

sealed interface FileTransferConfiguration {
    val host: String
    val port: Int
    val username: String
    val password: String?

    data class Ftp(
        override var host: String = "",
        override var port: Int = 21,
        override var username: String = "",
        override var password: String? = null
    ) : FileTransferConfiguration

    data class Sftp(
        override var host: String = "",
        override var port: Int = 22,
        override var username: String = "",
        override var password: String? = null,
        var privateKeyPath: String? = null
    ) : FileTransferConfiguration
}
