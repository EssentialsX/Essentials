package net.essentialsx.discord.interactions.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.PlayerNotFoundException;
import com.earth2me.essentials.utils.FormatUtil;
import net.essentialsx.discord.EssentialsJDA;
import net.essentialsx.discord.interactions.command.InteractionCommand;
import net.essentialsx.discord.interactions.command.InteractionCommandArgument;
import net.essentialsx.discord.interactions.command.InteractionCommandArgumentType;
import net.essentialsx.discord.interactions.command.InteractionEvent;
import net.essentialsx.discord.util.DiscordUtil;
import org.bukkit.Bukkit;

import static com.earth2me.essentials.I18n.tl;

public class MessageCommand extends InteractionCommand {
    public MessageCommand(EssentialsJDA jda) {
        super(jda, "msg", "Messages a player on the Minecraft Server.");
        addArgument(new InteractionCommandArgument("username", "The player to send the message to", InteractionCommandArgumentType.STRING, true));
        addArgument(new InteractionCommandArgument("message", "The message to send to the player", InteractionCommandArgumentType.STRING, true));
    }

    @Override
    public void onCommand(InteractionEvent event) {
        final boolean getHidden = DiscordUtil.hasRoles(event.getMember(), jda.getSettings().getCommandAdminSnowflakes(getName()));
        final User user;
        try {
            user = jda.getPlugin().getEss().matchUser(Bukkit.getServer(), null, event.getStringArgument("username"), getHidden, false);
        } catch (PlayerNotFoundException e) {
            event.reply("That user could not be found!");
            return;
        }

        if (!getHidden && user.isIgnoreMsg()) {
            event.reply(tl("msgIgnore", user.getDisplayName()));
            return;
        }

        if (user.isAfk()) {
            if (user.getAfkMessage() != null) {
                event.reply(tl("userAFKWithMessage", user.getDisplayName(), user.getAfkMessage()));
            } else {
                event.reply(tl("userAFK", user.getDisplayName()));
            }
        }

        final String message = DiscordUtil.hasRoles(event.getMember(), jda.getSettings().getPermittedFormattingRoles()) ?
                FormatUtil.replaceFormat(event.getStringArgument("message")) : FormatUtil.stripFormat(event.getStringArgument("message"));
        event.reply(tl("msgFormat", tl("meSender"), user.getDisplayName(), message));
        user.sendMessage(tl("msgFormat", event.getMember().getUser().getAsTag(), tl("meRecipient"), message));
    }
}
