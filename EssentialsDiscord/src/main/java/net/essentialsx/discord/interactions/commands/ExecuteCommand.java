package net.essentialsx.discord.interactions.commands;

import net.essentialsx.discord.interactions.command.InteractionCommand;
import net.essentialsx.discord.interactions.command.InteractionCommandArgument;
import net.essentialsx.discord.interactions.command.InteractionCommandArgumentType;

public class ExecuteCommand extends InteractionCommand {
    public ExecuteCommand() {
        super("execute", "Executes a console command on the Minecraft Server.");
        addArgument(new InteractionCommandArgument("command", "The command to be executed", InteractionCommandArgumentType.STRING, true));
    }
}
