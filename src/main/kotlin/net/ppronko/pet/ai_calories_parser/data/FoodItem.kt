package net.ppronko.pet.ai_calories_parser.data

data class FoodItem(
    val name: String,
    val weightGrams: Int,
    val calories: Int,
    val protein: Int,
    val fats: Int,
    val carbs: Int
)
