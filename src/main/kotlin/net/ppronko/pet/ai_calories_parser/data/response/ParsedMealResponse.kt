package net.ppronko.pet.ai_calories_parser.data.response

import com.fasterxml.jackson.annotation.JsonProperty
import net.ppronko.pet.ai_calories_parser.data.FoodItem
import net.ppronko.pet.ai_calories_parser.data.MealSummary

data class ParsedMealResponse(
    val mealName: String,
    val items: List<FoodItem>,
    @JsonProperty("summary")
    val mealSummary: MealSummary
)
