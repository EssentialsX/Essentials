package net.essentialsx.discord.util;

import com.earth2me.essentials.messaging.IMessageRecipient;
import com.earth2me.essentials.utils.FormatUtil;
import net.essentialsx.api.v2.services.discord.InteractionMember;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class DiscordMessageRecipient implements IMessageRecipient {
    private final InteractionMember member;
    private final AtomicBoolean died = new AtomicBoolean(false);

    public DiscordMessageRecipient(InteractionMember member) {
        this.member = member;
    }

    @Override
    public void sendMessage(String message) {
    }

    @Override
    public MessageResponse sendMessage(IMessageRecipient recipient, String message) {
        return MessageResponse.UNREACHABLE;
    }

    @Override
    public MessageResponse onReceiveMessage(IMessageRecipient sender, String message) {
        if (died.get()) {
            sender.setReplyRecipient(null);
            return MessageResponse.UNREACHABLE;
        }

        final String cleanMessage = MessageUtil.sanitizeDiscordMarkdown(FormatUtil.stripFormat(message));

        member.sendPrivateMessage(cleanMessage).thenAccept(success -> {
            if (!success) {
                died.set(true);
            }
        });
        return MessageResponse.SUCCESS;
    }

    @Override
    public String getName() {
        return member.getTag();
    }

    @Override
    public String getDisplayName() {
        return member.getTag();
    }

    @Override
    public boolean isReachable() {
        return !died.get();
    }

    @Override
    public IMessageRecipient getReplyRecipient() {
        return null;
    }

    @Override
    public void setReplyRecipient(IMessageRecipient recipient) {

    }

    @Override
    public boolean isHiddenFrom(Player player) {
        return died.get();
    }
}
