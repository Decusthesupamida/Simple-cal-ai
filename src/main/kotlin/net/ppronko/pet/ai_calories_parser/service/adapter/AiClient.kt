package net.ppronko.pet.ai_calories_parser.service.adapter

interface AiClient {
    fun generateContent(prompt: String): String
    fun generateContent(prompt: String, base64Image: String, mimeType: String = "image/jpeg"): String
}