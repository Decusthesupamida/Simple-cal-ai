package net.ppronko.pet.ai_calories_parser

import net.ppronko.pet.ai_calories_parser.config.properties.TelegramBotProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(TelegramBotProperties::class)
class AiCaloriesParserApplication

fun main(args: Array<String>) {
	runApplication<AiCaloriesParserApplication>(*args)
}
