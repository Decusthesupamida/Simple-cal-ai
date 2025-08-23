package net.ppronko.pet.ai_calories_parser.service

import net.ppronko.pet.ai_calories_parser.data.MacroGoals
import net.ppronko.pet.ai_calories_parser.data.entity.DailyGoal
import net.ppronko.pet.ai_calories_parser.data.entity.TelegramUser
import net.ppronko.pet.ai_calories_parser.repository.DailyGoalRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class DailyGoalService(
    private val dailyGoalRepository: DailyGoalRepository
) {

    @Transactional
    fun saveManualGoal(user: TelegramUser, date: LocalDate, goals: MacroGoals) {
        val existingGoal = dailyGoalRepository.findByUserAndDate(user, date).orElseThrow {
            IllegalStateException("Goal for user ${user.chatId} and date $date not found")
        }

        if (existingGoal != null) {
            existingGoal.calories = goals.calories
            existingGoal.protein = goals.proteins
            existingGoal.fats = goals.fats
            existingGoal.carbs = goals.carbs

            dailyGoalRepository.save(existingGoal)

        } else {
                val newGoal = DailyGoal(
                date = date,
                user = user,
                calories = goals.calories,
                protein = goals.proteins,
                fats = goals.fats,
                carbs = goals.carbs
            )
            dailyGoalRepository.save(newGoal)
        }
    }
}