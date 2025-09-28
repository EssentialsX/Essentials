package net.essentialsx.discord.interactions.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.PlayerNotFoundException;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.FormatUtil;
import net.essentialsx.api.v2.services.discord.InteractionCommandArgument;
import net.essentialsx.api.v2.services.discord.InteractionCommandArgumentType;
import net.essentialsx.api.v2.services.discord.InteractionEvent;
import net.essentialsx.discord.JDADiscordService;
import net.essentialsx.discord.interactions.InteractionCommandImpl;
import net.essentialsx.discord.util.DiscordMessageRecipient;
import net.essentialsx.discord.util.MessageUtil;
import org.bukkit.Bukkit;

import java.util.concurrent.atomic.AtomicReference;

import static com.earth2me.essentials.I18n.tlLiteral;

public class MessageCommand extends InteractionCommandImpl {
    public MessageCommand(JDADiscordService jda) {
        super(jda, "msg", tlLiteral("discordCommandMessageDescription"));
        addArgument(new InteractionCommandArgument("username", tlLiteral("discordCommandMessageArgumentUsername"), InteractionCommandArgumentType.STRING, true));
        addArgument(new InteractionCommandArgument("message", tlLiteral("discordCommandMessageArgumentMessage"), InteractionCommandArgumentType.STRING, true));
    }

    @Override
    public void onCommand(InteractionEvent event) {
        final boolean getHidden = event.getMember().hasRoles(getAdminSnowflakes());
        final User user;
        try {
            user = jda.getPlugin().getEss().matchUser(Bukkit.getServer(), null, event.getStringArgument("username"), getHidden, false);
        } catch (PlayerNotFoundException e) {
            event.replyTl("errorWithMessage", e.getMessage());
            return;
        }

        if (!getHidden && user.isIgnoreMsg()) {
            event.replyTl("msgIgnore", MessageUtil.sanitizeDiscordMarkdown(user.getDisplayName()));
            return;
        }

        if (user.isAfk()) {
            if (user.getAfkMessage() != null) {
                event.replyTl("userAFKWithMessage", MessageUtil.sanitizeDiscordMarkdown(user.getDisplayName()), MessageUtil.sanitizeDiscordMarkdown(user.getAfkMessage()));
            } else {
                event.replyTl("userAFK", MessageUtil.sanitizeDiscordMarkdown(user.getDisplayName()));
            }
        }

        final String message = event.getMember().hasRoles(jda.getSettings().getPermittedFormattingRoles()) ?
                FormatUtil.replaceFormat(event.getStringArgument("message")) : FormatUtil.stripFormat(event.getStringArgument("message"));
        event.replyTl("msgFormat", tlLiteral("meSender"), MessageUtil.sanitizeDiscordMarkdown(user.getDisplayName()), MessageUtil.sanitizeDiscordMarkdown(message));

        user.sendTl("msgFormat", event.getMember().getTag(), AdventureUtil.parsed(user.playerTl("meRecipient")), message);
        // We use an atomic reference here so that java will garbage collect the recipient
        final AtomicReference<DiscordMessageRecipient> ref = new AtomicReference<>(new DiscordMessageRecipient(event.getMember()));
        jda.getPlugin().getEss().runTaskLaterAsynchronously(() -> ref.set(null), 6000); // Expires after 5 minutes
        user.setReplyRecipient(ref.get());
    }
}
