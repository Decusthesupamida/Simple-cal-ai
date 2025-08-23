package net.ppronko.pet.ai_calories_parser.menu

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

object MainMenuKeyboard {

    const val ADD_MEAL = "Добавить запись 🍽️"
    const val GET_SUMMARY = "Сводка за сегодня 📈"
    const val VIEW_PROFILE = "Мой профиль 👤"
    const val HELP = "Помощь ℹ️"

    fun create(): ReplyKeyboardMarkup {
        val row1 = KeyboardRow().apply {
            add(KeyboardButton(ADD_MEAL))
            add(KeyboardButton(GET_SUMMARY))
        }

        val row2 = KeyboardRow().apply {
            add(KeyboardButton(VIEW_PROFILE))
            add(KeyboardButton(HELP))
        }

        return ReplyKeyboardMarkup().apply {
            keyboard = arrayListOf(row1, row2)
            resizeKeyboard = true
            oneTimeKeyboard = false
            selective = true
        }
    }
}