package net.ppronko.pet.ai_calories_parser.service.parse

import com.fasterxml.jackson.databind.ObjectMapper
import net.ppronko.pet.ai_calories_parser.data.*
import net.ppronko.pet.ai_calories_parser.data.response.ParsedMealResponse
import net.ppronko.pet.ai_calories_parser.service.adapter.AiClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service("mealTextParser")
class GeminiMealTextParser(
    private val geminiClient: AiClient,
    private val objectMapper: ObjectMapper
) : AiParser<MealParseInput, ParsedMealResponse> {

    private val logger = LoggerFactory.getLogger(javaClass)


    override fun parse(input: MealParseInput): ParsedMealResponse {
        val description = input.description
            ?: throw IllegalArgumentException("Text description must be provided for the text parser.")

        logger.info("Parsing meal from text description: '$description'")

        val prompt = createMealPrompt(description)

        val responseJson = geminiClient.generateContent(prompt)
        logger.debug("Received JSON response for text parsing: $responseJson")

        return objectMapper.readValue(responseJson, ParsedMealResponse::class.java)
    }

    private fun createMealPrompt(description: String): String {
        return """
            Ты — эксперт-диетолог и программист. Твоя задача — проанализировать текстовое описание приема пищи на русском языке и вернуть ТОЛЬКО JSON-объект строго определенной структуры.
            Не добавляй никакого другого текста, комментариев или markdown-форматирования (вроде ```json) вне самого JSON-объекта. Только чистый JSON.

            Структура JSON должна быть следующей:
            {
              "mealName": "краткое название приема пищи на русском",
              "items": [
                {
                  "name": "название продукта на русском",
                  "weightGrams": примерный_вес_в_граммах,
                  "calories": калории_для_этого_веса,
                  "protein": белки_в_граммах_для_этого_веса,
                  "fats": жиры_в_граммах_для_этого_веса,
                  "carbs": углеводы_в_граммах_для_этого_веса
                }
              ],
              "summary": {
                "totalCalories": общие_калории_за_весь_прием_пищи,
                "totalProtein": общие_белки_за_весь_прием_пищи,
                "totalFats": общие_жиры_за_весь_прием_пищи,
                "totalCarbs": общие_углеводы_за_весь_прием_пищи
              }
            }

            Придумай краткое, но емкое название для всего приема пищи в поле "mealName".
            Используй средние значения КБЖУ для продуктов. Оценивай вес максимально реалистично. Например: "чашка кофе" — 250г, "стакан молока" — 200г, "столовая ложка сахара" — 25г, "кусок хлеба" — 30г, "одно яйцо" — 55г.
            Обязательно просуммируй КБЖУ всех продуктов в секции "summary".

            Вот текст для анализа: "$description"
        """.trimIndent()
    }
}