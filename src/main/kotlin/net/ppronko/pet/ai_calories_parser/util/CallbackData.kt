package net.ppronko.pet.ai_calories_parser.util

data class CallbackData(val type: String, val payload: String) {
    override fun toString(): String = "$type:$payload"

    companion object {
        fun fromString(data: String): CallbackData? {
            val parts = data.split(":", limit = 2)
            return if (parts.size == 2) CallbackData(parts[0], parts[1]) else null
        }
    }
}
