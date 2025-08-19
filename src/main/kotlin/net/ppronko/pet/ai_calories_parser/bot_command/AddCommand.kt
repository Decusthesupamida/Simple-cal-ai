package net.ppronko.pet.ai_calories_parser.bot_command

import net.ppronko.pet.ai_calories_parser.data.BotCommandConstants
import net.ppronko.pet.ai_calories_parser.data.MealParseInput
import net.ppronko.pet.ai_calories_parser.data.ParsedMealResponse
import net.ppronko.pet.ai_calories_parser.data.UserState
import net.ppronko.pet.ai_calories_parser.menu.MainMenuKeyboard
import net.ppronko.pet.ai_calories_parser.pattern.Command
import net.ppronko.pet.ai_calories_parser.service.MealService
import net.ppronko.pet.ai_calories_parser.service.UserService
import net.ppronko.pet.ai_calories_parser.util.CallbackData
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.bots.AbsSender
import java.util.*

@Component
class AddCommand(
    private val mealService: MealService,
    private val userService: UserService
) : Command {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun getCommandName(): String = BotCommandConstants.ADD.command

    fun createAiResultKeyboard(sessionId: String, result: ParsedMealResponse): InlineKeyboardMarkup {
        val keyboard = InlineKeyboardMarkup.builder()

        result.items.forEachIndexed { index, item ->
            val buttonText = "✏️ ${item.name.take(20)} (~${item.weightGrams}г)" // Обрезаем длинные названия
            val callbackData = CallbackData("meal_edit_item", "$sessionId:$index").toString()

            keyboard.keyboardRow(listOf(
                InlineKeyboardButton.builder()
                    .text(buttonText)
                    .callbackData(callbackData)
                    .build()
            ))
        }

        keyboard.keyboardRow(listOf(
            InlineKeyboardButton.builder().text("✅ Сохранить всё").callbackData(CallbackData("meal_save", sessionId).toString()).build(),
            InlineKeyboardButton.builder().text("❌ Отмена").callbackData(CallbackData("meal_cancel", sessionId).toString()).build()
        ))

        return keyboard.build()
    }

    override fun execute(update: Update, sender: AbsSender) {
        val message = update.message
        val chatId = message.chatId
        val user = userService.getOrCreateUser(message.from)

        val input = createMealInputFromMessage(message)

        if (input.description.isNullOrBlank() && input.imageUrl.isNullOrBlank()) {

            userService.updateState(user, UserState.MEAL_AWAITING_DESCRIPTION)
            logger.info("User {} entered '/add' command waiting state.", user.chatId)

            sender.execute(SendMessage(chatId.toString(), "Отлично! Теперь отправь мне фото или текстовое описание того, что ты съел."))
            return
        }

        val sentMessage = sender.execute(SendMessage(chatId.toString(), "🧠 Анализирую, подождите несколько секунд..."))

        try {
            val parsedMeal = mealService.parseMealInput(input)

            val sessionId = UUID.randomUUID().toString()
            mealCache[sessionId] = parsedMeal

            val responseText = formatParsedMealResponse(parsedMeal)
            val keyboard = createAiResultKeyboard(sessionId, parsedMeal)

            val editMessage = EditMessageText().apply {
                this.chatId = chatId.toString()
                this.messageId = sentMessage.messageId
                this.text = responseText
                this.replyMarkup = keyboard
                this.parseMode = "Markdown"
            }

            sender.execute(editMessage)

        } catch (e: Exception) {
            logger.error("Failed to parse meal input for chatId {}", chatId, e)
            val errorMessage = "❌ К сожалению, не удалось распознать ваше описание. Попробуйте сформулировать по-другому или отправить другое фото."
            val editMessage = EditMessageText().apply {
                this.chatId = chatId.toString()
                this.messageId = sentMessage.messageId
                this.text = errorMessage
            }
            sender.execute(editMessage)
        } finally {
            userService.updateState(user, UserState.NONE)
        }
    }

    private fun createMealInputFromMessage(message: Message): MealParseInput {
        val text = message.text ?: message.caption

        val description = text
            ?.removePrefix(getCommandName())
            ?.removePrefix(MainMenuKeyboard.ADD_MEAL)
            ?.trim()

        val fileId = message.photo?.maxByOrNull { it.fileSize }?.fileId

        return MealParseInput(description, fileId)
    }

    fun formatParsedMealResponse(parsedMeal: ParsedMealResponse): String {
        val itemsText = parsedMeal.items.joinToString("\n") {
            "- ${it.name} (~${it.weightGrams}г): ${it.calories} ккал"
        }
        val summary = parsedMeal.mealSummary

        return """
            *${parsedMeal.mealName}*
            
            Я распознал следующее. Все верно?
            
            *Состав:*
            $itemsText
            
            *Итого:*
            🔥 Калории: *${summary.totalCalories} ккал*
            💪 Белки: *${summary.totalProtein} г*
            🥑 Жиры: *${summary.totalFats} г*
            🍞 Углеводы: *${summary.totalCarbs} г*
        """.trimIndent()
    }

}