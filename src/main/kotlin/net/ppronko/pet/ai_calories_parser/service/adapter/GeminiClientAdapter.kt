package net.ppronko.pet.ai_calories_parser.service.adapter

import net.ppronko.pet.ai_calories_parser.data.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Component("geminiClient")
class GeminiClientAdapter(
    private val geminiWebClient: WebClient,
    @Value("\${gemini.api.url}") private val apiUrl: String,
    @Value("\${gemini.api.key}") private val apiKey: String
) : AiClient  {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun generateContent(prompt: String): String {
        val requestBody = GeminiRequest(listOf(Content(listOf(Part(text = prompt)))))
        return executeRequest(apiUrl, requestBody)
    }

    override fun generateContent(prompt: String, base64Image: String, mimeType: String): String {
        val textPart = Part(text = prompt)
        val imagePart = Part(inlineData = InlineData(mimeType = mimeType, data = base64Image))
        val requestBody = GeminiRequest(listOf(Content(listOf(textPart, imagePart))))
        return executeRequest(apiUrl, requestBody)
    }
    private fun executeRequest(url: String, body: GeminiRequest): String {
        logger.info("Sending request to Gemini API at URL: $url")

        val response = geminiWebClient.post()
            .uri(apiUrl)
            .bodyValue(body)
            .retrieve()
            .onStatus({ it.isError }, { Mono.empty() })
            .bodyToMono<GeminiResponse>()
            .block()

        val responseText = response?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw RuntimeException("No content found in Gemini API response.")

        logger.info("Received raw response from Gemini.")
        return responseText.replace("```json", "").replace("```", "").trim()
    }
}