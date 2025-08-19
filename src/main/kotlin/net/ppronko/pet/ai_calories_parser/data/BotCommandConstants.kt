package net.ppronko.pet.ai_calories_parser.data

enum class BotCommandConstants(val command: String) {
    START("/start"),
    ADD("/add"),
    SUMMARY("/summary"),
    PROFILE("/profile"),
    ACTIVITY("/activity"),
    HELP("/help"),

    SET_AGE("/set_age"),
    SET_WEIGHT("/set_weight"),
    SET_HEIGHT("/set_height"),
    SET_GENDER("/set_gender");

    companion object {
        fun fromString(text: String): BotCommandConstants? {
            return entries.find { text.startsWith(it.command) }
        }
    }
}