package net.ppronko.pet.ai_calories_parser.data.request

data class UserProfileUpdateRequest(
    val gender: String?,
    val age: Int?,
    val weight: Double?,
    val height: Double?
)
