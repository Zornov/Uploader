package dev.zornov.uploader.configuration

data class SshConfiguration(
    var host: String = "",
    var port: Int = 22,
    var username: String = "",
    var password: String? = null,
    var privateKeyPath: String? = null
)