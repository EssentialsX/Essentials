package net.essentialsx.discord.interactions.commands;

import net.essentialsx.api.v2.services.discord.InteractionCommandArgument;
import net.essentialsx.api.v2.services.discord.InteractionCommandArgumentType;
import net.essentialsx.api.v2.services.discord.InteractionEvent;
import net.essentialsx.discord.JDADiscordService;
import net.essentialsx.discord.interactions.InteractionCommandImpl;
import net.essentialsx.discord.util.DiscordCommandSender;
import net.essentialsx.discord.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;

import static com.earth2me.essentials.I18n.tl;

public class ExecuteCommand extends InteractionCommandImpl {
    public ExecuteCommand(JDADiscordService jda) {
        super(jda, "execute", tl("discordCommandExecuteDescription"));
        addArgument(new InteractionCommandArgument("command", tl("discordCommandExecuteArgumentCommand"), InteractionCommandArgumentType.STRING, true));
    }

    @Override
    public void onCommand(final InteractionEvent event) {
        final String command = event.getStringArgument("command");
        event.reply(tl("discordCommandExecuteReply", command));
        Bukkit.getScheduler().runTask(jda.getPlugin(), () -> {
            try {
                Bukkit.dispatchCommand(new DiscordCommandSender(jda, Bukkit.getConsoleSender(), message -> event.reply(MessageUtil.sanitizeDiscordMarkdown(message))).getSender(), command);
            } catch (CommandException e) {
                // Check if this is a vanilla command, in which case we have to use a vanilla command sender :(
                if (e.getMessage().contains("a vanilla command listener") || (e.getCause() != null && e.getCause().getMessage().contains("a vanilla command listener"))) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    return;
                }
                // Something unrelated, should error out here
                throw e;
            }
        });
    }
}
