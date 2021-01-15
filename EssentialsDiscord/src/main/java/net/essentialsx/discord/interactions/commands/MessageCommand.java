package net.essentialsx.discord.interactions.commands;

import com.earth2me.essentials.User;
import net.essentialsx.discord.EssentialsJDA;
import net.essentialsx.discord.interactions.command.InteractionCommand;
import net.essentialsx.discord.interactions.command.InteractionCommandArgument;
import net.essentialsx.discord.interactions.command.InteractionCommandArgumentType;
import net.essentialsx.discord.interactions.command.InteractionEvent;
import org.bukkit.Bukkit;

public class MessageCommand extends InteractionCommand {
    private final EssentialsJDA jda;

    public MessageCommand(EssentialsJDA jda) {
        super("msg", "Messages a player on the Minecraft Server.");
        this.jda = jda;
        addArgument(new InteractionCommandArgument("username", "The player to send the message to", InteractionCommandArgumentType.STRING, true));
        addArgument(new InteractionCommandArgument("message", "The message to send to the player", InteractionCommandArgumentType.STRING, true));
    }

    @Override
    public void onCommand(InteractionEvent event) {
        //todo nickname
        final User user = jda.getPlugin().getEss().getUser(Bukkit.getPlayer(event.getStringArgument("username")));
        if (user == null || user.isVanished()) {
            event.replyEphemeral("That user could not be found!");
            return;
        }
        final String message = event.getStringArgument("message");
        user.sendMessage(message); //todo formatting/messagesender
    }
}
