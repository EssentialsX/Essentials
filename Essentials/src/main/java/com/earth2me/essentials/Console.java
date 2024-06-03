package com.earth2me.essentials;

import com.earth2me.essentials.messaging.IMessageRecipient;
import com.earth2me.essentials.messaging.SimpleMessageRecipient;
import com.earth2me.essentials.utils.AdventureUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.earth2me.essentials.I18n.tlLiteral;

public final class Console implements IMessageRecipient {
    public static final String NAME = "Console";
    public static final String DISPLAY_NAME = tlLiteral("consoleName");
    private static Console instance; // Set in essentials

    private final IEssentials ess;
    private final IMessageRecipient messageRecipient;

    private Console(final IEssentials ess) {
        this.ess = ess;
        this.messageRecipient = new SimpleMessageRecipient(ess, this);
    }

    public static Console getInstance() {
        return instance;
    }

    static void setInstance(final IEssentials ess) { // Called in Essentials#onEnable()
        instance = new Console(ess);
    }

    /**
     * @deprecated Use {@link Console#getCommandSender()}
     */
    @Deprecated
    public static CommandSender getCommandSender(final Server server) throws Exception {
        return server.getConsoleSender();
    }

    public CommandSender getCommandSender() {
        return ess.getServer().getConsoleSender();
    }

    @Override
    public String getName() {
        return Console.NAME;
    }

    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return Console.DISPLAY_NAME;
    }

    @Override
    public void sendMessage(final String message) {
        getCommandSender().sendMessage(message);
    }

    @Override
    public void sendTl(String tlKey, Object... args) {
        final String translation = tlLiteral(tlKey, args);
        if (translation.isEmpty()) {
            return;
        }

        final Audience consoleAudience = ((Essentials) ess).getBukkitAudience().sender(getCommandSender());
        final Component component = AdventureUtil.miniMessage()
                .deserialize(translation);
        consoleAudience.sendMessage(component);
    }

    @Override
    public String tlSender(String tlKey, Object... args) {
        return tlLiteral(tlKey, args);
    }

    @Override
    public boolean isReachable() {
        return true;
    }

    /* ================================
     * >> DELEGATE METHODS
     * ================================ */

    @Override
    public MessageResponse sendMessage(final IMessageRecipient recipient, final String message) {
        return this.messageRecipient.sendMessage(recipient, message);
    }

    @Override
    public MessageResponse onReceiveMessage(final IMessageRecipient sender, final String message) {
        return this.messageRecipient.onReceiveMessage(sender, message);
    }

    @Override
    public IMessageRecipient getReplyRecipient() {
        return this.messageRecipient.getReplyRecipient();
    }

    @Override
    public void setReplyRecipient(final IMessageRecipient recipient) {
        this.messageRecipient.setReplyRecipient(recipient);
    }

    @Override
    public boolean isHiddenFrom(Player player) {
        return false;
    }
}
