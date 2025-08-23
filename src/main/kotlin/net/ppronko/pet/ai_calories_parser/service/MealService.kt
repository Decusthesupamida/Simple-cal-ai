package net.ppronko.pet.ai_calories_parser.service

import net.ppronko.pet.ai_calories_parser.config.properties.TelegramBotProperties
import net.ppronko.pet.ai_calories_parser.data.DailySummaryDto
import net.ppronko.pet.ai_calories_parser.data.MacroGoals
import net.ppronko.pet.ai_calories_parser.data.MealParseInput
import net.ppronko.pet.ai_calories_parser.data.response.ParsedMealResponse
import net.ppronko.pet.ai_calories_parser.data.entity.FoodEntry
import net.ppronko.pet.ai_calories_parser.data.entity.TelegramUser
import net.ppronko.pet.ai_calories_parser.repository.DailyGoalRepository
import net.ppronko.pet.ai_calories_parser.repository.FoodEntryRepository
import net.ppronko.pet.ai_calories_parser.service.parse.AiParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.api.methods.GetFile
import java.time.LocalDate

@Service
class MealService(
    private val foodEntryRepository: FoodEntryRepository,
    private val mealTextParser: AiParser<MealParseInput, ParsedMealResponse>,
    private val mealVisionParser: AiParser<MealParseInput, ParsedMealResponse>,
    private val botProperties: TelegramBotProperties,
    private val userProfileService: UserProfileService,
    private val dailyGoalRepository: DailyGoalRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val sender = object : DefaultAbsSender(DefaultBotOptions()) {
        override fun getBotToken(): String = botProperties.token
    }

    fun parseMealInput(input: MealParseInput): ParsedMealResponse {
        val finalInput = if (input.imageUrl != null) {
            val file = sender.execute(GetFile(input.imageUrl))
            val fileUrl = file.getFileUrl( botProperties.token)
            logger.info("Constructed file URL for download: {}", fileUrl)
            input.copy(imageUrl = fileUrl)
        } else {
            input
        }

        return if (finalInput.imageUrl != null) {
            mealVisionParser.parse(finalInput)
        } else {
            mealTextParser.parse(finalInput)
        }
    }

    @Transactional
    fun saveParsedMeal(user: TelegramUser, parsedMeal: ParsedMealResponse): FoodEntry {
        val summary = parsedMeal.mealSummary
        val foodEntry = FoodEntry(
            mealName = parsedMeal.mealName,
            entryDate = LocalDate.now(),
            totalCalories = summary.totalCalories,
            totalProtein = summary.totalProtein,
            totalFats = summary.totalFats,
            totalCarbs = summary.totalCarbs,
            user = user
        )
        val savedEntry = foodEntryRepository.save(foodEntry)
        logger.info("Saved new food entry with id {} for user {}", savedEntry.id, user.chatId)
        return savedEntry
    }

    /**
     * Собирает все записи о еде для пользователя за сегодняшний день,
     * суммирует КБЖУ и формирует отформатированную сводку.
     *
     * @param user Сущность пользователя, для которого нужно получить сводку.
     * @return Готовая к отправке в Telegram строка со сводкой за день.
     */
    @Transactional(readOnly = true)
    fun getTodaysSummary(user: TelegramUser): DailySummaryDto {
        val todayEntries = foodEntryRepository.findAllByUserAndEntryDate(user, LocalDate.now())
        val goal = getGoalForToday(user)

        // 2. Считаем съеденное
        val consumed = MacroGoals(
            calories = todayEntries.sumOf { it.totalCalories },
            proteins = todayEntries.sumOf { it.totalProtein },
            fats = todayEntries.sumOf { it.totalFats },
            carbs = todayEntries.sumOf { it.totalCarbs }
        )

        val remaining = MacroGoals(
            calories = (goal.calories - consumed.calories).coerceAtLeast(0),
            proteins = (goal.proteins - consumed.proteins).coerceAtLeast(0),
            fats = (goal.fats - consumed.fats).coerceAtLeast(0),
            carbs = (goal.carbs - consumed.carbs).coerceAtLeast(0)
        )

        return DailySummaryDto(
            entries = todayEntries,
            consumed = consumed,
            goal = goal,
            remaining = remaining
        )
    }

    @Transactional(readOnly = true)
    fun getGoalForDate(user: TelegramUser, date: LocalDate): MacroGoals {
        val profile = user.profile
            ?: throw IllegalStateException("User ${user.chatId} has no profile.")

        if (profile.areGoalsAutomatic) {
            return userProfileService.getCalculatedGoals(user)
        }

        val goalForDate = dailyGoalRepository.findByUserAndDate(user, date)

        if (goalForDate.isPresent) {
            return goalForDate.get().toMacroGoals()
        }

        val latestGoal = dailyGoalRepository.findFirstByUserAndDateLessThanEqualOrderByDateDesc(user, date)
        if (latestGoal != null) {
            return latestGoal.toMacroGoals()
        }

        return userProfileService.getCalculatedGoals(user)
    }

    // Вспомогательный метод для удобства
    fun getGoalForToday(user: TelegramUser): MacroGoals {
        return getGoalForDate(user, LocalDate.now())
    }

}