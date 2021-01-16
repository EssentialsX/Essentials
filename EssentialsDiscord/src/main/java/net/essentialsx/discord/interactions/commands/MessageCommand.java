package net.essentialsx.discord.interactions.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.PlayerNotFoundException;
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
        final boolean getHidden = false; //todo config for admins seeing hidden users
        final User user;
        try {
            user = jda.getPlugin().getEss().matchUser(Bukkit.getServer(), null, event.getStringArgument("username"), getHidden, false);
        } catch (PlayerNotFoundException e) {
            event.replyEphemeral("That user could not be found!");
            return;
        }

        final String message = event.getStringArgument("message");
        user.sendMessage(message); //todo formatting/messagesender
    }
}
