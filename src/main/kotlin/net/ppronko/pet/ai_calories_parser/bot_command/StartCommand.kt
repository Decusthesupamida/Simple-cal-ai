package net.ppronko.pet.ai_calories_parser.bot_command

import net.ppronko.pet.ai_calories_parser.data.BotCommandConstants
import net.ppronko.pet.ai_calories_parser.data.UserState
import net.ppronko.pet.ai_calories_parser.menu.MainMenuKeyboard
import net.ppronko.pet.ai_calories_parser.pattern.Command
import net.ppronko.pet.ai_calories_parser.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class StartCommand(
    private val userService: UserService
) : Command {
    override fun getCommandName(): String = BotCommandConstants.START.command
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun execute(update: Update, sender: AbsSender) {
        val chatId = update.message.chatId
        val telegramUser = update.message.from

        val user = userService.getOrCreateUser(telegramUser)
        logger.info("Processing /start command for user {}", user.chatId)

        user.state = UserState.NONE
        userService.save(user)

        val welcomeText = "Привет, ${user.firstName ?: "пользователь"}! 👋\n\n" +
                "Я твой личный помощник по подсчету калорий. " +
                "Используй меню внизу или отправляй команды напрямую."

        val message = SendMessage(chatId.toString(), welcomeText).apply {
            replyMarkup = MainMenuKeyboard.create()
        }

        sender.execute(message)
    }
}