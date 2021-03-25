package net.essentialsx.discord.util;

import com.earth2me.essentials.messaging.IMessageRecipient;
import com.earth2me.essentials.utils.FormatUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.earth2me.essentials.I18n.tl;

public class DiscordMessageRecipient implements IMessageRecipient {
    private final Member member;
    private final AtomicBoolean died = new AtomicBoolean(false);

    public DiscordMessageRecipient(Member member) {
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

        final CompletableFuture<PrivateChannel> future = member.getUser().openPrivateChannel().submit();
        future.thenCompose(privateChannel -> privateChannel.sendMessage("**" + tl("replyFromDiscord", sender.getName()) + "** `" + cleanMessage + "`").submit())
                .whenComplete((m, error) -> {
                    if (error != null) {
                        died.set(true);
                    }
                });
        return MessageResponse.SUCCESS;
    }

    @Override
    public String getName() {
        return member.getUser().getAsTag();
    }

    @Override
    public String getDisplayName() {
        return member.getUser().getAsTag();
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
