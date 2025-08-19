package net.ppronko.pet.ai_calories_parser.bot_command

import net.ppronko.pet.ai_calories_parser.data.BotCommandConstants
import net.ppronko.pet.ai_calories_parser.data.MacroGoals
import net.ppronko.pet.ai_calories_parser.data.entity.TelegramUserProfile
import net.ppronko.pet.ai_calories_parser.menu.ProfileKeyboard
import net.ppronko.pet.ai_calories_parser.pattern.Command
import net.ppronko.pet.ai_calories_parser.service.MealService
import net.ppronko.pet.ai_calories_parser.service.UserProfileService
import net.ppronko.pet.ai_calories_parser.service.UserService
import net.ppronko.pet.ai_calories_parser.util.MarkdownV2Escaper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class ProfileCommand(
    private val userProfileService: UserProfileService,
    private val userService: UserService,
    private val mealService: MealService
) : Command {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun getCommandName(): String = BotCommandConstants.PROFILE.command

    override fun execute(update: Update, sender: AbsSender) {
        val chatId = update.message.chatId
        val user = userService.getOrCreateUser(update.message.from)
        logger.info("Processing /profile command for chatId {}", chatId)

        val profile = userProfileService.getProfile(chatId)

        if (!profile.isConfigured) {
            val setupText = "👋 Похоже, вы здесь впервые! Давайте настроим ваш профиль, чтобы расчеты были точными.\n\n" +
                    "Пожалуйста, воспользуйтесь кнопками ниже, чтобы ввести ваши данные."

            val message = SendMessage(chatId.toString(), setupText).apply {
                replyMarkup = ProfileKeyboard.create(profile)
            }
            sender.execute(message)

        } else {
            val currentGoals = mealService.getGoalForToday(user)
            val calculatedGoals = userProfileService.getCalculatedGoals(user)

            val responseText = formatProfile(profile, currentGoals, calculatedGoals)

            val message = SendMessage(chatId.toString(), responseText).apply {
                replyMarkup = ProfileKeyboard.create(profile)
                parseMode = "Markdown"
            }
            sender.execute(message)
        }
    }

    fun formatProfile(profile: TelegramUserProfile, currentGoals: MacroGoals, calculatedGoals: MacroGoals): String {
        val mode = if (profile.areGoalsAutomatic) "Автоматический" else "Ручной"
        val activity = MarkdownV2Escaper.escape(profile.activityDescription)

        val currentGoalsText = "🔥 *${currentGoals.calories} ккал* | 💪 *${currentGoals.protein}г* | 🥑 *${currentGoals.fats}г* | 🍞 *${currentGoals.carbs}г*"
        val calculatedGoalsText = "🔥 ${calculatedGoals.calories} ккал | 💪 ${calculatedGoals.protein}г | 🥑 ${calculatedGoals.fats}г | 🍞 ${calculatedGoals.carbs}г"

        return """
            *Ваш профиль* 👤
            - Вес: ${profile.weight} кг, Рост: ${profile.height} см, Возраст: ${profile.age}
            - Активность: `$activity`
            
            *Текущие цели (Режим: $mode)*
            $currentGoalsText
            
            *Рекомендуемые цели (на основе профиля):*
            `$calculatedGoalsText`
        """.trimIndent()
    }
}