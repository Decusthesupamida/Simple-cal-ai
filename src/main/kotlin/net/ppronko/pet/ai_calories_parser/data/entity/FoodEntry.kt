package net.ppronko.pet.ai_calories_parser.data.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import net.ppronko.pet.ai_calories_parser.data.UserState
import java.time.LocalDate

@Entity
@Table(name = "food_entries")
data class FoodEntry(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",

    @Column(name = "meal_name", nullable = false)
    val mealName: String,

    @Column(name = "entry_date", nullable = false)
    val entryDate: LocalDate,

    @Column(name = "total_calories", nullable = false)
    val totalCalories: Int,

    @Column(name = "total_protein", nullable = false)
    val totalProtein: Int,

    @Column(name = "total_fats", nullable = false)
    val totalFats: Int,

    @Column(name = "total_carbs", nullable = false)
    val totalCarbs: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_chat_id", nullable = false)
    val user: TelegramUser
) {

    protected constructor() : this(
        mealName = "",
        entryDate = LocalDate.now(),
        totalCalories = 0,
        totalProtein = 0,
        totalFats = 0,
        totalCarbs = 0,
        user = TelegramUser(0, null, null, UserState.NONE)
    )
}