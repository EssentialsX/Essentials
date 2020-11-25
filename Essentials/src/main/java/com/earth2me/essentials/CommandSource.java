package com.earth2me.essentials;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSource {
    protected CommandSender sender;

    public CommandSource(final CommandSender base) {
        this.sender = base;
    }

    public final CommandSender getSender() {
        return sender;
    }

    public final Player getPlayer() {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        return null;
    }

    public final net.ess3.api.IUser getUser(final IEssentials ess) {
        if (sender instanceof Player) {
            return ess.getUser((Player) sender);
        }
        return null;
    }

    public final boolean isPlayer() {
        return sender instanceof Player;
    }

    public final CommandSender setSender(final CommandSender base) {
        return this.sender = base;
    }

    public void sendMessage(final String message) {
        if (!message.isEmpty()) {
            sender.sendMessage(message);
        }
    }

    public boolean isAuthorized(final String permission, final IEssentials ess) {
        return !(sender instanceof Player) || getUser(ess).isAuthorized(permission);
    }

    public String getSelfSelector() {
        return sender instanceof Player ? getPlayer().getName() : "*";
    }

    public String getDisplayName() {
        return sender instanceof Player ? getPlayer().getDisplayName() : getSender().getName();
    }
}
