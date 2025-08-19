package net.ppronko.pet.ai_calories_parser.data

data class GeminiResponse(
    val candidates: List<Candidate>?,
    val error: GeminiError?
)
data class Candidate(val content: Content)
data class GeminiError(val message: String)
