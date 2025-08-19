package net.ppronko.pet.ai_calories_parser.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "telegram.bot")
data class TelegramBotProperties(
    val username: String,
    val token: String
)