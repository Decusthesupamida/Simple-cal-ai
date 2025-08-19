package net.ppronko.pet.ai_calories_parser.service

import net.ppronko.pet.ai_calories_parser.data.Gender
import net.ppronko.pet.ai_calories_parser.data.MacroGoals
import net.ppronko.pet.ai_calories_parser.data.entity.TelegramUserProfile
import org.springframework.stereotype.Service

@Service
class GoalCalculationService {

    /**
     * Рассчитывает цели по КБЖУ на основе профиля пользователя.
     */
    fun calculateGoals(profile: TelegramUserProfile): MacroGoals {
        val bmr = calculateBMR(profile)
        val tdee = calculateTDEE(bmr, profile.activityCoefficient)
        return calculateMacros(tdee)
    }

    private fun calculateBMR(profile: TelegramUserProfile): Double {
        return when (profile.gender) {
            Gender.MALE -> (10 * profile.weight) + (6.25 * profile.height) - (5 * profile.age) + 5
            Gender.FEMALE -> (10 * profile.weight) + (6.25 * profile.height) - (5 * profile.age) - 161
        }
    }

    private fun calculateTDEE(bmr: Double, coefficient: Double): Int {
        return (bmr * coefficient).toInt()
    }

    private fun calculateMacros(calories: Int): MacroGoals {
        val protein = (calories * 0.3 / 4).toInt()
        val fats = (calories * 0.3 / 9).toInt()
        val carbs = (calories * 0.4 / 4).toInt()
        return MacroGoals(calories, protein, fats, carbs)
    }
}