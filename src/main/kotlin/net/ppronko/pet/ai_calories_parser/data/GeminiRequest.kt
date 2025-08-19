package net.ppronko.pet.ai_calories_parser.data

import com.fasterxml.jackson.annotation.JsonInclude

data class GeminiRequest(val contents: List<Content>)

data class Content(val parts: List<Part>)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Part (
    val text: String? = null,
    val inlineData: InlineData? = null
)

data class InlineData (
    val mimeType: String,
    val data: String
)
