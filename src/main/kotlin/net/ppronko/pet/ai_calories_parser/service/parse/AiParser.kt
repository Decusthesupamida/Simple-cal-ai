package net.ppronko.pet.ai_calories_parser.service.parse

interface AiParser<in I, out O> {
    fun parse(input: I): O
}