package net.ppronko.pet.ai_calories_parser.controller

import net.ppronko.pet.ai_calories_parser.controller.MiniAppProfileController.Companion.BASE_URL
import net.ppronko.pet.ai_calories_parser.data.MacroGoals
import net.ppronko.pet.ai_calories_parser.data.request.SetGoalsModeRequest
import net.ppronko.pet.ai_calories_parser.data.request.UserProfileUpdateRequest
import net.ppronko.pet.ai_calories_parser.service.miniapp.MiniAppUserProfileBridge
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(BASE_URL)
@CrossOrigin(origins = ["*"])
class MiniAppProfileController(
    private val userProfileService: MiniAppUserProfileBridge
) {

    @GetMapping(ID_PATH)
    fun getProfile(@PathVariable chatId: String): ResponseEntity<Any> {
        val profile = userProfileService.getProfile(chatId.toLong())
        return ResponseEntity.ok(profile)
    }

    @PutMapping(ID_PATH)
    fun updateProfile(
        @PathVariable chatId: Long,
        @RequestBody updateRequest: UserProfileUpdateRequest
    ): ResponseEntity<Any> {
        val updatedProfile = userProfileService.updateProfile(chatId, updateRequest)
        return ResponseEntity.ok(updatedProfile)
    }

    @PutMapping(ID_PATH + ACTIVITY_PATH)
    fun updateActivity(
        @PathVariable chatId: Long,
        @RequestBody activityRequest: Map<String, String>
    ): ResponseEntity<Any> {
        val description = activityRequest["description"]
            ?: return ResponseEntity.badRequest().body("Missing 'description' field")

        val updatedProfile = userProfileService.updateActivity(chatId, description)
        return ResponseEntity.ok(updatedProfile)
    }

    @PutMapping(ID_PATH + GOALS_MODE_PATH)
    fun setGoalsMode(
        @PathVariable chatId: Long,
        @RequestBody goalsRequest: SetGoalsModeRequest
    ): ResponseEntity<Any> {
        val updatedProfile = userProfileService.setGoalsMode(chatId, goalsRequest.automatic)
        return ResponseEntity.ok(updatedProfile)
    }

    @GetMapping(ID_PATH + GOALS_PATH)
    fun getCalculatedGoals(@PathVariable chatId: Long): ResponseEntity<MacroGoals> {
        val goals = userProfileService.getCalculatedGoals(chatId)
        return ResponseEntity.ok(goals)
    }

    companion object {
        const val BASE_URL = "/api/profile"
        const val ID_PATH  = "/{chatId}"
        const val ACTIVITY_PATH  = "/activity"
        const val GOALS_PATH  = "/goals"
        const val GOALS_MODE_PATH  = "/goals-mode"
    }

}