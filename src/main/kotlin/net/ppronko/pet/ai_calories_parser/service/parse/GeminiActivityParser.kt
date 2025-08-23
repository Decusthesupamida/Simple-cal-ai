package net.ppronko.pet.ai_calories_parser.service.parse

import net.ppronko.pet.ai_calories_parser.service.GeminiService
import net.ppronko.pet.ai_calories_parser.service.adapter.GeminiClientAdapter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class GeminiActivityParser(
    private val aiClient: GeminiClientAdapter
) : GeminiService<Double> {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun parse(description: String): Double {
        val prompt = createPrompt(description)

        logger.info("Parsing activity description: '$description'")
        val responseText = aiClient.generateContent(prompt)

        val coefficient = responseText.replace(",", ".").toDoubleOrNull()

        if (coefficient == null) {
            logger.warn("Failed to parse coefficient from Gemini response: '$responseText'. Returning default 1.55")
            return 1.55
        }

        logger.info("Successfully parsed activity coefficient: $coefficient")
        return coefficient
    }

    override fun createPrompt(description: String): String {
        return """
            Ты - эксперт по фитнесу и диетологии. Твоя задача - проанализировать текстовое описание физической активности человека и вернуть ТОЛЬКО ОДНО число - коэффициент TDEE (Total Daily Energy Expenditure).

            Основывайся на следующих общепринятых значениях:
            - 1.2: Сидячий образ жизни, нет тренировок.
            - 1.375: Легкая активность, тренировки 1-3 раза в неделю.
            - 1.55: Умеренная активность, тренировки 3-5 раз в неделю.
            - 1.725: Высокая активность, интенсивные тренировки 6-7 раз в неделю.
            - 1.9: Экстремальная активность, тяжелая физическая работа или тренировки дважды в день.
            
            Проанализируй текст и верни наиболее подходящее число. Ты можешь возвращать и промежуточные значения (например, 1.45), если считаешь это уместным.
            В ответе должно быть ТОЛЬКО число в формате "1.55", без каких-либо пояснений или текста.

            Текст для анализа: "$description"
        """.trimIndent()
    }
}