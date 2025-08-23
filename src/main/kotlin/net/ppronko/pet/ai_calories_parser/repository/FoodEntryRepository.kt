package net.ppronko.pet.ai_calories_parser.repository

import net.ppronko.pet.ai_calories_parser.data.entity.FoodEntry
import net.ppronko.pet.ai_calories_parser.data.entity.TelegramUser
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface FoodEntryRepository : JpaRepository<FoodEntry, String> {
    fun findAllByUserAndEntryDate(user: TelegramUser, date: LocalDate): List<FoodEntry>

    fun findAllByUserAndEntryDateBetween(user: TelegramUser, start: LocalDate, end: LocalDate): List<FoodEntry>
}