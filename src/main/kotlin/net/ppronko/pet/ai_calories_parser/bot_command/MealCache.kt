package net.ppronko.pet.ai_calories_parser.bot_command

import net.ppronko.pet.ai_calories_parser.data.ParsedMealResponse
import java.util.concurrent.ConcurrentHashMap

val mealCache = ConcurrentHashMap<String, ParsedMealResponse>()