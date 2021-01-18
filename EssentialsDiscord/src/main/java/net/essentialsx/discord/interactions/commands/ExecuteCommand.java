package net.essentialsx.discord.interactions.commands;

import net.essentialsx.discord.EssentialsJDA;
import net.essentialsx.discord.interactions.command.InteractionCommand;
import net.essentialsx.discord.interactions.command.InteractionCommandArgument;
import net.essentialsx.discord.interactions.command.InteractionCommandArgumentType;
import net.essentialsx.discord.interactions.command.InteractionEvent;
import net.essentialsx.discord.util.DiscordCommandSender;
import org.bukkit.Bukkit;

import static com.earth2me.essentials.I18n.tl;

public class ExecuteCommand extends InteractionCommand {
    public ExecuteCommand(EssentialsJDA jda) {
        super(jda, "execute", tl("discordCommandExecuteDescription"));
        addArgument(new InteractionCommandArgument("command", tl("discordCommandExecuteArgumentCommand"), InteractionCommandArgumentType.STRING, true));
    }

    @Override
    public void onCommand(InteractionEvent event) {
        final String command = event.getStringArgument("command");
        event.reply(tl("discordCommandExecuteReply", command));
        Bukkit.getScheduler().runTask(jda.getPlugin(), () ->
                Bukkit.dispatchCommand(new DiscordCommandSender(jda, Bukkit.getConsoleSender(), event::reply), command));
    }
}
