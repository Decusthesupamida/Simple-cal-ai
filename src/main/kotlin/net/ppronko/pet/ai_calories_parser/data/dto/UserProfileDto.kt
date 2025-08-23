package net.ppronko.pet.ai_calories_parser.data.dto

data class UserProfileDto(
    val chatId: Long,
    val firstName: String?,
    val username: String?,
    val gender: String,
    val age: Int,
    val weight: Double,
    val height: Double,
    val activityDescription: String,
    val activityCoefficient: Double?,
    val areGoalsAutomatic: Boolean,
    val isConfigured: Boolean
)
