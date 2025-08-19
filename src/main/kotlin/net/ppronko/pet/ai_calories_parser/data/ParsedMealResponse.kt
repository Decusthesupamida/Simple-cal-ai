package net.ppronko.pet.ai_calories_parser.data

import com.fasterxml.jackson.annotation.JsonProperty

data class ParsedMealResponse(
    val mealName: String,
    val items: List<FoodItem>,
    @JsonProperty("summary")
    val mealSummary: MealSummary
)
