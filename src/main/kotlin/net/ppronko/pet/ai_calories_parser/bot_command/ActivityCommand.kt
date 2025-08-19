package net.ppronko.pet.ai_calories_parser.bot_command

import net.ppronko.pet.ai_calories_parser.data.BotCommandConstants
import net.ppronko.pet.ai_calories_parser.pattern.Command
import net.ppronko.pet.ai_calories_parser.service.UserProfileService
import net.ppronko.pet.ai_calories_parser.service.UserService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class ActivityCommand(
    private val userService: UserService,
    private val userProfileService: UserProfileService
) : Command {
    override fun getCommandName(): String = BotCommandConstants.ACTIVITY.command

    override fun execute(update: Update, sender: AbsSender) {
        val chatId = update.message.chatId
        val description = update.message.text.removePrefix(getCommandName()).trim()

        if (description.isBlank()) {
            val response = "Пожалуйста, опишите вашу среднюю недельную активность после команды. Например:\n`/activity Три раза в неделю хожу в зал, в основном силовые тренировки.`"
            sender.execute(SendMessage(chatId.toString(), response))
            return
        }

        val sentMessage = sender.execute(SendMessage(chatId.toString(), "🧠 Анализирую вашу активность..."))

        try {
            val user = userService.getUser(chatId)
            val updatedProfile = userProfileService.updateUserActivity(user, description)
            val responseText = "✅ Ваш профиль обновлен! На основе вашего описания, ваш новый коэффициент активности: *${"%.2f".format(updatedProfile.activityCoefficient)}*.\n\nЦели будут автоматически пересчитаны, если включен автоматический режим."

            sender.execute(SendMessage(chatId.toString(), responseText).apply { parseMode = "Markdown" })
        } catch (e: Exception) {
            val errorMessage = "❌ Произошла ошибка при анализе. Попробуйте позже."
            sender.execute(SendMessage(chatId.toString(), errorMessage))
        } finally {
            // Можно удалить сообщение "Анализирую...", но это усложнит код, оставим так для простоты
        }
    }
}