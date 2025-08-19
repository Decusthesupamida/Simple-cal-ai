package net.ppronko.pet.ai_calories_parser.data

import net.ppronko.pet.ai_calories_parser.data.entity.FoodEntry

data class DailySummaryDto(
    val entries: List<FoodEntry>,
    val consumed: MacroGoals,
    val goal: MacroGoals,
    val remaining: MacroGoals
)