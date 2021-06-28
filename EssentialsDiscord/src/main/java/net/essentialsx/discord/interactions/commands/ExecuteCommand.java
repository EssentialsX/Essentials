package net.essentialsx.discord.interactions.commands;

import net.essentialsx.api.v2.services.discord.InteractionCommandArgument;
import net.essentialsx.api.v2.services.discord.InteractionCommandArgumentType;
import net.essentialsx.api.v2.services.discord.InteractionEvent;
import net.essentialsx.discord.JDADiscordService;
import net.essentialsx.discord.interactions.InteractionCommandImpl;
import net.essentialsx.discord.util.DiscordCommandSender;
import org.bukkit.Bukkit;

import static com.earth2me.essentials.I18n.tl;

public class ExecuteCommand extends InteractionCommandImpl {
    public ExecuteCommand(JDADiscordService jda) {
        super(jda, "execute", tl("discordCommandExecuteDescription"));
        addArgument(new InteractionCommandArgument("command", tl("discordCommandExecuteArgumentCommand"), InteractionCommandArgumentType.STRING, true));
    }

    @Override
    public void onCommand(InteractionEvent event) {
        final String command = event.getStringArgument("command");
        event.reply(tl("discordCommandExecuteReply", command));
        Bukkit.getScheduler().runTask(jda.getPlugin(), () ->
                Bukkit.dispatchCommand(new DiscordCommandSender(jda, Bukkit.getConsoleSender(), event::reply).getSender(), command));
    }
}
