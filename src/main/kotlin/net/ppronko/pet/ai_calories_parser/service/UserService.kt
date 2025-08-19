package net.ppronko.pet.ai_calories_parser.service

import net.ppronko.pet.ai_calories_parser.data.UserState
import net.ppronko.pet.ai_calories_parser.data.entity.TelegramUser
import net.ppronko.pet.ai_calories_parser.data.entity.TelegramUserProfile
import net.ppronko.pet.ai_calories_parser.repository.TelegramUserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.telegram.telegrambots.meta.api.objects.User

@Service
class UserService(
    private val userRepository: TelegramUserRepository,
) {
    private val logger: Logger = getLogger(UserService::class.java)

    @Transactional
    fun save(user: TelegramUser): TelegramUser {
        return userRepository.save(user)
    }

    @Transactional
    fun getUser(chatId: Long): TelegramUser {
        return userRepository.findByChatId(chatId)
            .orElseThrow { IllegalStateException("User not found for chatId: $chatId") }
    }

    @Transactional
    fun updateState(user: TelegramUser, newState: UserState) {
        user.state = newState
        userRepository.save(user)
    }

    @Transactional
    fun getOrCreateUser(telegramUser: User): TelegramUser {
        return userRepository.findByChatId(telegramUser.id).orElseGet {
            logger.info("Creating new user with chatId: ${telegramUser.id} and username: ${telegramUser.userName}")
            val newUser = TelegramUser(
                chatId = telegramUser.id,
                firstName = telegramUser.firstName,
                username = telegramUser.userName
            )
            newUser.addProfile(TelegramUserProfile())
            userRepository.save(newUser)
        }
    }

}
