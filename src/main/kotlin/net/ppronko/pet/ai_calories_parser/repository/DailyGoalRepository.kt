package net.ppronko.pet.ai_calories_parser.repository;

import net.ppronko.pet.ai_calories_parser.data.entity.DailyGoal
import net.ppronko.pet.ai_calories_parser.data.entity.TelegramUser
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface DailyGoalRepository : JpaRepository<DailyGoal, String> {
    fun findByUserAndDate(user: TelegramUser, date: LocalDate): DailyGoal?

    fun findFirstByUserAndDateLessThanEqualOrderByDateDesc(user: TelegramUser, date: LocalDate): DailyGoal?
}