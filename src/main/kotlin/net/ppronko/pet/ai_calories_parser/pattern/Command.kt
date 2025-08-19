package net.ppronko.pet.ai_calories_parser.pattern

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

interface Command {
    fun getCommandName(): String
    fun execute(update: Update, sender: AbsSender)
}