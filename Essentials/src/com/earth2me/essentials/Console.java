package com.earth2me.essentials;

import com.earth2me.essentials.messaging.IMessageRecipient;
import com.earth2me.essentials.messaging.SimpleMessageRecipient;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;


/**
 * <p>Console class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public final class Console implements IMessageRecipient {
    /** Constant <code>NAME="Console"</code> */
    public static final String NAME = "Console";
    private static Console instance; // Set in essentials
    
    private final IEssentials ess;
    private final IMessageRecipient messageRecipient;

    /**
     * <p>Getter for the field <code>instance</code>.</p>
     *
     * @return a {@link com.earth2me.essentials.Console} object.
     */
    public static Console getInstance() {
        return instance;
    }

    static void setInstance(IEssentials ess) { // Called in Essentials#onEnable()
        instance = new Console(ess);
    }
    
    /**
     * <p>getCommandSender.</p>
     *
     * @deprecated Use {@link com.earth2me.essentials.Console#getCommandSender()}
     * @param server a {@link org.bukkit.Server} object.
     * @return a {@link org.bukkit.command.CommandSender} object.
     * @throws java.lang.Exception if any.
     */
    @Deprecated
    public static CommandSender getCommandSender(Server server) throws Exception {
        return server.getConsoleSender();
    }

    private Console(IEssentials ess) {
        this.ess = ess;
        this.messageRecipient = new SimpleMessageRecipient(ess, this);
    }

    /**
     * <p>getCommandSender.</p>
     *
     * @return a {@link org.bukkit.command.CommandSender} object.
     */
    public CommandSender getCommandSender() {
        return ess.getServer().getConsoleSender();
    }

    /** {@inheritDoc} */
    @Override public String getName() {
        return Console.NAME;
    }

    /** {@inheritDoc} */
    @Override public String getDisplayName() {
        return Console.NAME;
    }

    /** {@inheritDoc} */
    @Override public void sendMessage(String message) {
        getCommandSender().sendMessage(message);
    }

    /** {@inheritDoc} */
    @Override public boolean isReachable() {
        return true;
    }
    
    /* ================================
     * >> DELEGATE METHODS
     * ================================ */

    /** {@inheritDoc} */
    @Override public MessageResponse sendMessage(IMessageRecipient recipient, String message) {
        return this.messageRecipient.sendMessage(recipient, message);
    }

    /** {@inheritDoc} */
    @Override public MessageResponse onReceiveMessage(IMessageRecipient sender, String message) {
        return this.messageRecipient.onReceiveMessage(sender, message);
    }

    /** {@inheritDoc} */
    @Override public IMessageRecipient getReplyRecipient() {
        return this.messageRecipient.getReplyRecipient();
    }

    /** {@inheritDoc} */
    @Override public void setReplyRecipient(IMessageRecipient recipient) {
        this.messageRecipient.setReplyRecipient(recipient);
    }
}
