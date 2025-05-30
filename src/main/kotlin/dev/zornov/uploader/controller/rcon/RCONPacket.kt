package dev.zornov.uploader.controller.rcon

data class RCONPacket(
    val size: Int,
    val requestId: Int,
    val responseId: Int,
    val type: RequestType,
    val payload: ByteArray,
) {
    enum class RequestType(val id: Int) {
        COMMAND_RESPONSE(0),
        COMMAND(2),
        LOGIN(3);

        companion object {
            private val ids = entries.associateBy { it.id }
            fun getById(id: Int) = ids[id] ?: error("Unknown RequestType id: $id")
        }
    }
}
