package net.essentialsx.discord.interactions.commands;

import net.essentialsx.discord.EssentialsJDA;
import net.essentialsx.discord.interactions.command.InteractionCommand;
import net.essentialsx.discord.interactions.command.InteractionCommandArgument;
import net.essentialsx.discord.interactions.command.InteractionCommandArgumentType;
import net.essentialsx.discord.interactions.command.InteractionEvent;
import net.essentialsx.discord.util.DiscordCommandSender;
import org.bukkit.Bukkit;

public class ExecuteCommand extends InteractionCommand {
    public ExecuteCommand(EssentialsJDA jda) {
        super(jda, "execute", "Executes a console command on the Minecraft Server.");
        addArgument(new InteractionCommandArgument("command", "The command to be executed", InteractionCommandArgumentType.STRING, true));
    }

    @Override
    public void onCommand(InteractionEvent event) {
        //TODO PERM CHECKS

        Bukkit.getScheduler().runTask(jda.getPlugin(), () ->
                Bukkit.dispatchCommand(new DiscordCommandSender(jda, Bukkit.getConsoleSender(), event::reply), event.getStringArgument("command")));
    }
}
