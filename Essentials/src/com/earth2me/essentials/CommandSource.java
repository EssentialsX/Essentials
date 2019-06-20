package com.earth2me.essentials;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * <p>CommandSource class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class CommandSource {
    protected CommandSender sender;

    /**
     * <p>Constructor for CommandSource.</p>
     *
     * @param base a {@link org.bukkit.command.CommandSender} object.
     */
    public CommandSource(final CommandSender base) {
        this.sender = base;
    }

    /**
     * <p>Getter for the field <code>sender</code>.</p>
     *
     * @return a {@link org.bukkit.command.CommandSender} object.
     */
    public final CommandSender getSender() {
        return sender;
    }

    /**
     * <p>getPlayer.</p>
     *
     * @return a {@link org.bukkit.entity.Player} object.
     */
    public final Player getPlayer() {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        return null;
    }

    /**
     * <p>isPlayer.</p>
     *
     * @return a boolean.
     */
    public final boolean isPlayer() {
        return (sender instanceof Player);
    }

    /**
     * <p>Setter for the field <code>sender</code>.</p>
     *
     * @param base a {@link org.bukkit.command.CommandSender} object.
     * @return a {@link org.bukkit.command.CommandSender} object.
     */
    public final CommandSender setSender(final CommandSender base) {
        return this.sender = base;
    }


    /**
     * <p>sendMessage.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public void sendMessage(String message) {
        if (!message.isEmpty()) {
            sender.sendMessage(message);
        }
    }
}
