package net.ppronko.pet.ai_calories_parser.service

interface GeminiService<T> {
    fun parse(description: String): T
    fun createPrompt(description: String): String
}