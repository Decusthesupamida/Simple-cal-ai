package net.ppronko.pet.ai_calories_parser.controller

import net.ppronko.pet.ai_calories_parser.controller.GoalController.Companion.BASE_URL
import net.ppronko.pet.ai_calories_parser.data.dto.DailyGoalDto
import net.ppronko.pet.ai_calories_parser.service.miniapp.MiniAppGoalBridge
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping(BASE_URL)
@CrossOrigin(origins = ["*"])
class GoalController(
    private val goalService: MiniAppGoalBridge
) {
    companion object {
        const val BASE_URL = "/api/goals"
        const val CHAT_ID_PATH  = "/{chatId}"
    }

    @GetMapping(CHAT_ID_PATH)
    fun getGoals(@PathVariable chatId: Long, @RequestParam date: LocalDate): ResponseEntity<Any> {
        try {
            val goals = goalService.getGoalForDate(chatId, date)
            return ResponseEntity.ok(goals)
        } catch (e: IllegalStateException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping(CHAT_ID_PATH)
    fun setGoals(@PathVariable chatId: Long, @RequestBody goal: DailyGoalDto): ResponseEntity<Any> {
        try {
            val goals = goalService.saveGoals(chatId, goal)
            return ResponseEntity.ok(goals)
        } catch (e: IllegalStateException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }
}