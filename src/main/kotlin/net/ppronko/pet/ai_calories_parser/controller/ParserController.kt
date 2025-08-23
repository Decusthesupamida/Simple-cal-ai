package net.ppronko.pet.ai_calories_parser.controller

import net.ppronko.pet.ai_calories_parser.data.MealParseInput
import net.ppronko.pet.ai_calories_parser.service.parse.GeminiActivityParser
import net.ppronko.pet.ai_calories_parser.service.parse.GeminiMealTextParser
import net.ppronko.pet.ai_calories_parser.service.parse.GeminiMealVisionParser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ParserController.BASE_URL)
@CrossOrigin(origins = ["*"])
class ParserController(
    private val geminiMealTextParser: GeminiMealTextParser,
    private val geminiMealVisionParser: GeminiMealVisionParser,
    private val geminiActivityParser: GeminiActivityParser
) {

    companion object {
        const val BASE_URL = "/api/parser"
        const val MEAL_PATH = "/meal"
        const val TEXT_PARSE_PATH = "/text"
        const val IMAGE_PARSE_PATH = "/image"
        const val ACTIVITY_PATH = "/activity"
    }

    @PostMapping(MEAL_PATH + TEXT_PARSE_PATH)
    fun parseMealByText(@RequestBody request: MealParseInput): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(geminiMealTextParser.parse(request))
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().body(ex.message)
        }
    }

    @PostMapping(MEAL_PATH + IMAGE_PARSE_PATH)
    fun parseMealByImage(@RequestBody request: MealParseInput): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(geminiMealVisionParser.parse(request))
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().body(ex.message)
        }
    }

    @PostMapping(ACTIVITY_PATH)
    fun parseActivity(@RequestParam description: String): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(geminiActivityParser.parse(description))
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().body(ex.message)
        }
    }

}