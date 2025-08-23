package net.ppronko.pet.ai_calories_parser.service.miniapp

import net.ppronko.pet.ai_calories_parser.data.Gender
import net.ppronko.pet.ai_calories_parser.data.MacroGoals
import net.ppronko.pet.ai_calories_parser.data.dto.UserProfileDto
import net.ppronko.pet.ai_calories_parser.data.entity.TelegramUser
import net.ppronko.pet.ai_calories_parser.data.entity.TelegramUserProfile
import net.ppronko.pet.ai_calories_parser.data.request.UserProfileUpdateRequest
import net.ppronko.pet.ai_calories_parser.repository.TelegramUserProfileRepository
import net.ppronko.pet.ai_calories_parser.repository.TelegramUserRepository
import net.ppronko.pet.ai_calories_parser.service.parse.GeminiActivityParser
import net.ppronko.pet.ai_calories_parser.service.GoalCalculationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MiniAppUserProfileBridge(
    private val userRepository: TelegramUserRepository,
    private val userProfileRepository: TelegramUserProfileRepository,
    private val activityParser: GeminiActivityParser,
    private val goalCalculationService: GoalCalculationService
) {

    fun getProfile(chatId: Long): UserProfileDto {
        val user = userRepository.findById(chatId)
            .orElseThrow { IllegalStateException("User not found") }

        val profile = user.profile
            ?: throw IllegalStateException("Profile not found")

        return profile.toDto(user)
    }

    @Transactional
    fun updateProfile(chatId: Long, update: UserProfileUpdateRequest): UserProfileDto {
        val user = userRepository.findById(chatId)
            .orElseThrow { IllegalStateException("User not found") }

        val profile = user.profile
            ?: throw IllegalStateException("Profile not found")

        update.gender?.let { profile.gender = Gender.valueOf(it.uppercase()) }
        update.age?.let { profile.age = it }
        update.weight?.let { profile.weight = it }
        update.height?.let { profile.height = it }
        profile.isConfigured = true

        userRepository.save(user)

        return profile.toDto(user)
    }

    @Transactional
    fun updateActivity(chatId: Long, description: String): UserProfileDto {
        val user = userRepository.findById(chatId)
            .orElseThrow { IllegalStateException("User not found") }

        val profile = user.profile ?: throw IllegalStateException("Profile not found")
        profile.activityDescription = description
        profile.activityCoefficient = activityParser.parse(description)

        userProfileRepository.save(profile)

        return profile.toDto(user)
    }

    @Transactional
    fun setGoalsMode(chatId: Long, automatic: Boolean): UserProfileDto {
        val user = userRepository.findById(chatId)
            .orElseThrow { IllegalStateException("User not found") }

        val profile = user.profile ?: throw IllegalStateException("Profile not found")
        profile.areGoalsAutomatic = automatic

        userProfileRepository.save(profile)

        return profile.toDto(user)
    }

    fun getCalculatedGoals(chatId: Long): MacroGoals {
        val user = userRepository.findById(chatId)
            .orElseThrow { IllegalStateException("User not found") }

        val profile = user.profile ?: throw IllegalStateException("Profile not found")

        return goalCalculationService.calculateGoals(profile)
    }

    private fun TelegramUserProfile.toDto(user: TelegramUser) = UserProfileDto(
        chatId = user.chatId,
        firstName = user.firstName,
        username = user.username,
        gender = this.gender.name,
        age = this.age,
        weight = this.weight,
        height = this.height,
        activityDescription = this.activityDescription,
        activityCoefficient = this.activityCoefficient,
        areGoalsAutomatic = this.areGoalsAutomatic,
        isConfigured = this.isConfigured
    )
}