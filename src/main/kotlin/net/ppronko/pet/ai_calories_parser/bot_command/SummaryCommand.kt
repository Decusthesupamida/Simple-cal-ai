package net.ppronko.pet.ai_calories_parser.bot_command

import net.ppronko.pet.ai_calories_parser.data.BotCommandConstants
import net.ppronko.pet.ai_calories_parser.pattern.Command
import net.ppronko.pet.ai_calories_parser.service.MealService
import net.ppronko.pet.ai_calories_parser.service.UserService
import net.ppronko.pet.ai_calories_parser.util.MessageFormatter
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class SummaryCommand(
    private val userService: UserService,
    private val mealService: MealService
) : Command {

    override fun getCommandName(): String = BotCommandConstants.SUMMARY.command

    override fun execute(update: Update, sender: AbsSender) {
        val chatId = update.message.chatId
        val user = userService.getOrCreateUser(update.message.from)

        val summaryDto = mealService.getTodaysSummary(user)

        val responseText = MessageFormatter.formatSummary(summaryDto)

        sender.execute(SendMessage(chatId.toString(), responseText).apply {
            parseMode = "Html"
        })
    }
}