package net.ppronko.pet.ai_calories_parser.bot_command

import net.ppronko.pet.ai_calories_parser.data.BotCommandConstants
import net.ppronko.pet.ai_calories_parser.pattern.Command
import net.ppronko.pet.ai_calories_parser.util.MarkdownV2Escaper
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class HelpCommand : Command {

    override fun getCommandName(): String = BotCommandConstants.HELP.command // Убедитесь, что здесь BotCommand, а не BotCommandConstants

    override fun execute(update: Update, sender: AbsSender) {
        val chatId = update.message.chatId


        val helpText = """
            <b>Добро пожаловать в Easy Calories Bot</b> 🤖
            
            Я помогу тебе легко и быстро отслеживать калории и БЖУ, используя мощь искусственного интеллекта.
            
            <b>Основные возможности:</b>
            
            <b>1. 🍽️ Добавление еды</b>
            Нажми кнопку "<i>Добавить запись 🍽️</i>" или отправь команду <code>/add</code>. Ты можешь:
            - <i>Описать еду текстом:</i> Просто напиши, что ты съел, например: <code>овсянка на молоке с бананом</code>. Я сам все проанализирую и рассчитаю.
            - <i>Отправить фото:</i> Прикрепи фотографию своего блюда, и я постараюсь распознать его состав. Ты также можешь добавить текстовый комментарий к фото для уточнения.
            
            <b>2. 📈 Просмотр сводки</b>
            Нажми кнопку "<i>Сводка за сегодня 📈</i>" или отправь команду <code>/summary</code>, чтобы увидеть полный отчет по КБЖУ за текущий день, а также сколько еще осталось до твоей цели.
            
            <b>3. 👤 Управление профилем</b>
            Нажми кнопку "<i>Мой профиль 👤</i>" или отправь команду <code>/profile</code>. В этом разделе ты можешь:
            - Указать свои параметры (пол, возраст, вес, рост).
            - Описать свою физическую активность своими словами, чтобы я рассчитал для тебя персональный коэффициент.
            - Переключаться между <i>автоматическим</i> расчетом целей (на основе твоего профиля) и <i>ручной</i> установкой.
            
            <b>4. 🆘 Помощь</b>
            Команда <code>/help</code> или кнопка "<i>Помощь ℹ️</i>" всегда покажут это сообщение.
            
            Желаю успехов в достижении твоих целей! ✨
        """.trimIndent()

        sender.execute(SendMessage(chatId.toString(), helpText).apply {
            parseMode = "Html"
        })
    }
}