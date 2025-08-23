package net.ppronko.pet.ai_calories_parser.data.dto

import java.time.LocalDate

data class DailyGoalDto(
    val calories: Int,
    val carbs: Int,
    val proteins: Int,
    val fats: Int,
    val date: LocalDate
)
