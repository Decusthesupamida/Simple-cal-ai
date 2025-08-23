package net.ppronko.pet.ai_calories_parser.service.miniapp

import net.ppronko.pet.ai_calories_parser.data.dto.DailyGoalDto
import net.ppronko.pet.ai_calories_parser.data.entity.DailyGoal
import net.ppronko.pet.ai_calories_parser.repository.DailyGoalRepository
import net.ppronko.pet.ai_calories_parser.repository.TelegramUserRepository
import net.ppronko.pet.ai_calories_parser.service.UserProfileService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class MiniAppGoalBridge(
    private val goalRepository: DailyGoalRepository,
    private val userRepository: TelegramUserRepository,
    private val userProfileService: UserProfileService
) {
    @Transactional(readOnly = true)
    fun getGoalForDate(chatId: Long, date: LocalDate): DailyGoalDto {

        val user = userRepository.findById(chatId).orElseThrow { IllegalStateException("User not found") }

        val profile = user.profile
            ?: throw IllegalStateException("User ${user.chatId} has no profile.")

        if (profile.areGoalsAutomatic) {
            val calculatedGoals = userProfileService.getCalculatedGoals(user)

            return DailyGoalDto(
                date = date,
                calories = calculatedGoals.calories,
                proteins = calculatedGoals.proteins,
                fats = calculatedGoals.fats,
                carbs = calculatedGoals.carbs
            )
        }

        val goalForDate = goalRepository.findByUserAndDate(user, date)

        if (goalForDate.isPresent) {
            return goalForDate.get().toDto(date)
        }

        val latestGoal = goalRepository.findFirstByUserAndDateLessThanEqualOrderByDateDesc(user, date)
        if (latestGoal != null) {
            return latestGoal.toDto(date)
        }

        val calculatedGoals = userProfileService.getCalculatedGoals(user)

        return DailyGoalDto(
            date = date,
            calories = calculatedGoals.calories,
            proteins = calculatedGoals.proteins,
            fats = calculatedGoals.fats,
            carbs = calculatedGoals.carbs
        )
    }

    @Transactional
    fun saveGoals(chatId: Long, goal: DailyGoalDto): DailyGoalDto {
        val user = userRepository.findById(chatId).orElseThrow { IllegalStateException("User not found") }

        val profile = user.profile ?: throw IllegalStateException("Profile not found")

        if (profile.areGoalsAutomatic) {
            throw IllegalStateException("Goals are automatic")
        }

        val foundGoal = goalRepository.findByUserAndDate(user, goal.date)

        if (goalRepository.findByUserAndDate(user, goal.date).isEmpty) {
            DailyGoal(
                user = user,
                date = goal.date,
                calories = goal.calories,
                protein = goal.proteins,
                fats = goal.fats,
                carbs = goal.carbs
            ).let {
                val toDto = goalRepository.save(it).toDto(goal.date)
                return toDto
            }
        } else {
            foundGoal.get().calories = goal.calories
            foundGoal.get().protein = goal.proteins
            foundGoal.get().fats = goal.fats
            foundGoal.get().carbs = goal.carbs
            val toDto = goalRepository.save(foundGoal.get()).toDto(goal.date)
            return toDto
        }
    }

    private fun DailyGoal.toDto(date: LocalDate): DailyGoalDto = DailyGoalDto(calories, protein, fats, carbs, date)
}