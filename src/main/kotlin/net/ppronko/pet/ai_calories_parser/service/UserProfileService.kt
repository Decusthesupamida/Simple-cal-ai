package net.ppronko.pet.ai_calories_parser.service

import net.ppronko.pet.ai_calories_parser.data.MacroGoals
import net.ppronko.pet.ai_calories_parser.data.UserProfileUpdateDto
import net.ppronko.pet.ai_calories_parser.data.entity.TelegramUser
import net.ppronko.pet.ai_calories_parser.data.entity.TelegramUserProfile
import net.ppronko.pet.ai_calories_parser.repository.TelegramUserProfileRepository
import net.ppronko.pet.ai_calories_parser.repository.TelegramUserRepository
import net.ppronko.pet.ai_calories_parser.service.parse.GeminiActivityParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserProfileService (
    private val userRepository: TelegramUserRepository,
    private val userProfileRepository: TelegramUserProfileRepository,
    private val activityParser: GeminiActivityParser,
    private val goalCalculationService: GoalCalculationService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun getProfile(chatId: Long): TelegramUserProfile {
        val profile = userRepository.findById(chatId)
            .orElseThrow { IllegalStateException("User not found for chatId: $chatId") }

        return profile.profile
            ?: throw IllegalStateException("User profile not found for chatId: $chatId. This should not happen.")
    }

    @Transactional
    fun updateProfileDetails(user: TelegramUser, updateDto: UserProfileUpdateDto): TelegramUserProfile? {

        val profile = user.profile
            ?: throw IllegalStateException("User profile not found. This should not happen.")

        updateDto.gender?.let { profile.gender = it }
        updateDto.age?.let { profile.age = it }
        updateDto.weight?.let { profile.weight = it }
        updateDto.height?.let { profile.height = it }
        profile.isConfigured = true

        val saveUser = userRepository.save(user)

        return saveUser.profile
    }


    @Transactional
    fun updateUserActivity(user: TelegramUser, description: String): TelegramUserProfile {
        val profile = user.profile
            ?: throw IllegalStateException("User profile not found. This should not happen.")


        val coefficient = activityParser.parse(description)

        profile.activityDescription = description
        profile.activityCoefficient = coefficient

        return userProfileRepository.save(profile)
    }

    @Transactional
    fun setGoalsMode(user: TelegramUser, isAutomatic: Boolean): TelegramUserProfile {
        val profile = user.profile
            ?: throw IllegalStateException("User profile not found. This should not happen.")
        profile.areGoalsAutomatic = isAutomatic
        logger.info("Set goals mode for user ${user.chatId} to ${if (isAutomatic) "AUTOMATIC" else "MANUAL"}")
        return userProfileRepository.save(profile)
    }

    fun getCalculatedGoals(user: TelegramUser): MacroGoals {
        val profile = user.profile
            ?: throw IllegalStateException("User profile not found. This should not happen.")

        return goalCalculationService.calculateGoals(profile)
    }
}