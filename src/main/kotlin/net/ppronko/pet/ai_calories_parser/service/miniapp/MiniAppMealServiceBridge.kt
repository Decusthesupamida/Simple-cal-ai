package net.ppronko.pet.ai_calories_parser.service.miniapp

import net.ppronko.pet.ai_calories_parser.data.MealParseInput
import net.ppronko.pet.ai_calories_parser.data.dto.FoodEntryDto
import net.ppronko.pet.ai_calories_parser.data.entity.FoodEntry
import net.ppronko.pet.ai_calories_parser.repository.FoodEntryRepository
import net.ppronko.pet.ai_calories_parser.repository.TelegramUserRepository
import net.ppronko.pet.ai_calories_parser.service.MealService
import net.ppronko.pet.ai_calories_parser.service.parse.GeminiMealTextParser
import net.ppronko.pet.ai_calories_parser.service.parse.GeminiMealVisionParser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@Service
class MiniAppMealServiceBridge(
    private val mealRepository: FoodEntryRepository,
    private val mealVisionParser: GeminiMealVisionParser,
    private val telegramUserRepository: TelegramUserRepository,
    private val mealService: MealService,
    private val mealTextParser: GeminiMealTextParser
) {

    @Transactional
    fun createManualEntry(userId: Long, food: FoodEntryDto) {
        val user = telegramUserRepository.findById(userId)
            .orElseThrow { IllegalStateException("User not found") }

        val entry = FoodEntry(
            mealName = food.mealName,
            entryDate = food.date,
            totalCalories = food.cals,
            totalProtein = food.proteins,
            totalFats = food.fats,
            totalCarbs = food.carbs,
            user = user
        )

        mealRepository.save(entry).toDto()
    }

    @Transactional
    fun createMealFromImage(chatId: Long, image: MultipartFile, description: String?): FoodEntryDto {
        val telegramUser = telegramUserRepository.findById(chatId)
            .orElseThrow { IllegalStateException("User not found") }

        val input = MealParseInput(
            imageFile = image.bytes,
            description = description
        )

        val parsedMeal = mealVisionParser.parse(input)

        val savedEntry = mealService.saveParsedMeal(telegramUser, parsedMeal)

        return savedEntry.toDto()
    }

    @Transactional
    fun createMealFromText(chatId: Long, text: String): FoodEntryDto {
        val telegramUser = telegramUserRepository.findById(chatId)
            .orElseThrow { IllegalStateException("User not found") }

        val input = MealParseInput(
            imageUrl = null,
            description = text
        )

        val parsedMeal = mealTextParser.parse(input)

        val savedEntry = mealService.saveParsedMeal(telegramUser, parsedMeal)

        return savedEntry.toDto()
    }

    @Transactional
    fun deleteMeal(chatId: Long, mealId: String) {
        telegramUserRepository.findById(chatId).orElseThrow { IllegalStateException("User not found") }
        mealRepository.findById(mealId).ifPresent { mealRepository.delete(it) }
    }

    fun getSummary(chatId: Long, start: LocalDate, end: LocalDate): List<FoodEntryDto> {
        val user = telegramUserRepository.findById(chatId).orElseThrow { IllegalStateException("User not found") }
        return mealRepository.findAllByUserAndEntryDateBetween(user, start, end).map { it.toDto() }
    }

    private fun FoodEntry.toDto(): FoodEntryDto = FoodEntryDto(
        id = this.id,
        mealName = this.mealName,
        cals = this.totalCalories,
        proteins = this.totalProtein,
        fats = this.totalFats,
        carbs = this.totalCarbs,
        date = this.entryDate
    )


}
