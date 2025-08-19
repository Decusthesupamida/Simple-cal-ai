package net.ppronko.pet.ai_calories_parser.data

data class UserProfileUpdateDto(
    val gender: Gender? = null,
    val age: Int? = null,
    val weight: Double? = null,
    val height: Double? = null
)

enum class Gender {
    MALE,
    FEMALE
}