package net.ppronko.pet.ai_calories_parser.util

object MarkdownV2Escaper {
    private val RESERVED_CHARS = listOf("_", "*", "[", "]", "(", ")", "~", "`", ">", "#", "+", "-", "=", "|", "{", "}", ".", "!")

    fun escape(text: String): String {
        return RESERVED_CHARS.fold(text) { acc, char ->
            acc.replace(char, "\\$char")
        }
    }
}