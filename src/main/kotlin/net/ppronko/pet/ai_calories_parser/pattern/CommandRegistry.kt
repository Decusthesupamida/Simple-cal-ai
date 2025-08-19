package net.ppronko.pet.ai_calories_parser.pattern

import org.springframework.stereotype.Component

@Component
class CommandRegistry(
    commands: List<Command>
) {

    private val commandMap: Map<String, Command> = commands.associateBy { it.getCommandName() }

    fun getCommand(commandKey: String): Command? {
        return commandMap[commandKey]
    }
}