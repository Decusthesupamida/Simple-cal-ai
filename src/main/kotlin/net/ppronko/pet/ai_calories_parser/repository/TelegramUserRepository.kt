package net.ppronko.pet.ai_calories_parser.repository;

import net.ppronko.pet.ai_calories_parser.data.entity.TelegramUser
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface TelegramUserRepository : JpaRepository<TelegramUser, Long> {

    fun findByChatId(chatId: Long): Optional<TelegramUser>
}