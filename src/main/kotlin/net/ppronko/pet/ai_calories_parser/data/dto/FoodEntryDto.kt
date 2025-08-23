package net.ppronko.pet.ai_calories_parser.data.dto

import java.time.LocalDate

data class FoodEntryDto(
    var id: String?,
    var mealName: String,
    var cals: Int,
    var proteins: Int,
    var fats: Int,
    var carbs: Int,
    var date: LocalDate
)
