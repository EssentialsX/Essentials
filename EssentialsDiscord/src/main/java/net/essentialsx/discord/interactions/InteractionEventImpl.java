package net.essentialsx.discord.interactions;

import com.earth2me.essentials.utils.FormatUtil;
import com.google.common.base.Joiner;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.essentialsx.api.v2.services.discord.InteractionChannel;
import net.essentialsx.api.v2.services.discord.InteractionEvent;
import net.essentialsx.api.v2.services.discord.InteractionMember;
import net.essentialsx.discord.util.DiscordUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class which provides information about what triggered an interaction event.
 */
public class InteractionEventImpl implements InteractionEvent {
    private final static Logger logger = Logger.getLogger("EssentialsDiscord");
    private final SlashCommandEvent event;
    private final InteractionMember member;
    private final List<String> replyBuffer = new ArrayList<>();

    public InteractionEventImpl(final SlashCommandEvent jdaEvent) {
        this.event = jdaEvent;
        this.member = new InteractionMemberImpl(jdaEvent.getMember());
    }

    @Override
    public void reply(String message) {
        message = FormatUtil.stripFormat(message).replace("§", ""); // Don't ask
        replyBuffer.add(message);
        String reply = Joiner.on('\n').join(replyBuffer);
        reply = reply.substring(0, Math.min(Message.MAX_CONTENT_LENGTH, reply.length()));
        event.getHook().editOriginal(
                new MessageBuilder()
                        .setContent(reply)
                        .setAllowedMentions(DiscordUtil.NO_GROUP_MENTIONS).build())
                .queue(null, error -> logger.log(Level.SEVERE, "Error while editing command interaction response", error));
    }

    @Override
    public InteractionMember getMember() {
        return member;
    }

    @Override
    public String getStringArgument(String key) {
        final OptionMapping mapping = event.getOption(key);
        return mapping == null ? null : mapping.getAsString();
    }

    @Override
    public Long getIntegerArgument(String key) {
        final OptionMapping mapping = event.getOption(key);
        return mapping == null ? null : mapping.getAsLong();
    }

    @Override
    public Boolean getBooleanArgument(String key) {
        final OptionMapping mapping = event.getOption(key);
        return mapping == null ? null : mapping.getAsBoolean();
    }

    @Override
    public InteractionMember getUserArgument(String key) {
        final OptionMapping mapping = event.getOption(key);
        return mapping == null ? null : new InteractionMemberImpl(mapping.getAsMember());
    }

    @Override
    public InteractionChannel getChannelArgument(String key) {
        final OptionMapping mapping = event.getOption(key);
        return mapping == null ? null : new InteractionChannelImpl(mapping.getAsGuildChannel());
    }

    @Override
    public String getChannelId() {
        return event.getChannel().getId();
    }
}
