package net.ppronko.pet.ai_calories_parser.controller

import net.ppronko.pet.ai_calories_parser.controller.MealController.Companion.BASE_URL
import net.ppronko.pet.ai_calories_parser.data.dto.FoodEntryDto
import net.ppronko.pet.ai_calories_parser.data.request.FoodParsedRequest
import net.ppronko.pet.ai_calories_parser.service.miniapp.MiniAppMealServiceBridge
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@RestController
@RequestMapping(BASE_URL)
@CrossOrigin(origins = ["*"])
class MealController(
    private val miniAppMealServiceBridge: MiniAppMealServiceBridge
) {
    companion object {
        const val BASE_URL = "/api/meals"
        const val CHAT_ID_PATH  = "/{chatId}"
        const val TEXT_PATH  = "/text"
        const val IMAGE_PATH  = "/image"
        const val MEAL_ID_PATH  = "/{mealId}"
        const val SUMMARY_PATH  = "/summary"
    }

    @PostMapping(CHAT_ID_PATH)
    fun createManualMeal(@PathVariable chatId: Long, @RequestBody foodEntry: FoodEntryDto): ResponseEntity<Any> {
        val meals = miniAppMealServiceBridge.createManualEntry(chatId, foodEntry)
        return ResponseEntity.ok(meals)
    }

    @PostMapping(CHAT_ID_PATH + TEXT_PATH)
    fun createManualMealByText(@PathVariable chatId: Long, @RequestBody request: FoodParsedRequest): ResponseEntity<Any> {
        val meals = miniAppMealServiceBridge.createMealFromText(chatId, request.description)
        return ResponseEntity.ok(meals)
    }

    @PostMapping(CHAT_ID_PATH + IMAGE_PATH)
    fun createManualMealByImage(@PathVariable chatId: Long,
                                @RequestParam file: MultipartFile,
                                @RequestParam(required = false) description: String?): ResponseEntity<Any> {
        val meals = miniAppMealServiceBridge.createMealFromImage(chatId, file, description)
        return ResponseEntity.ok(meals)
    }

    @DeleteMapping(CHAT_ID_PATH + MEAL_ID_PATH)
    fun deleteMeal(@PathVariable chatId: Long, @PathVariable mealId: String): ResponseEntity<Any> {
        val meals = miniAppMealServiceBridge.deleteMeal(chatId, mealId)
        return ResponseEntity.ok(meals)
    }

    @GetMapping(CHAT_ID_PATH + SUMMARY_PATH)
    fun getSummary(@PathVariable chatId: Long, @RequestParam start: LocalDate, @RequestParam end: LocalDate): ResponseEntity<Any> {
        val summary = miniAppMealServiceBridge.getSummary(chatId, start, end)
        return ResponseEntity.ok(summary)
    }

}