package com.earth2me.essentials;

import com.earth2me.essentials.utils.AdventureUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.earth2me.essentials.I18n.tlLiteral;

public class CommandSource {
    protected Essentials ess;
    protected CommandSender sender;

    public CommandSource(final Essentials ess, final CommandSender base) {
        this.ess = ess;
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

    public void sendTl(final String tlKey, final Object... args) {
        if (isPlayer()) {
            //noinspection ConstantConditions
            getUser().sendTl(tlKey, args);
            return;
        }

        final String translation = tlLiteral(tlKey, args);
        if (!translation.startsWith(AdventureUtil.MINI_MESSAGE_PREFIX)) {
            sendMessage(translation);
            return;
        }
        sendComponent(AdventureUtil.miniMessage().deserialize(translation.substring(AdventureUtil.MINI_MESSAGE_PREFIX.length())));
    }

    public String tl(final String tlKey, final Object... args) {
        if (isPlayer()) {
            //noinspection ConstantConditions
            return getUser().playerTl(tlKey, args);
        }
        return tlLiteral(tlKey, args);
    }

    public Component tlComponent(final String tlKey, final Object... args) {
        if (isPlayer()) {
            //noinspection ConstantConditions
            return getUser().tlComponent(tlKey, args);
        }
        final String translation = tlLiteral(tlKey, args);
        if (!translation.startsWith(AdventureUtil.MINI_MESSAGE_PREFIX)) {
            return AdventureUtil.deserializeLegacy(translation);
        }
        return AdventureUtil.miniMessage().deserialize(translation.substring(AdventureUtil.MINI_MESSAGE_PREFIX.length()));
    }

    public void sendComponent(final Component component) {
        final BukkitAudiences audiences = ess.getBukkitAudience();
        audiences.sender(sender).sendMessage(component);
    }

    public final net.ess3.api.IUser getUser() {
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

    public boolean isAuthorized(final String permission) {
        //noinspection ConstantConditions
        return !(sender instanceof Player) || getUser().isAuthorized(permission);
    }

    public String getSelfSelector() {
        //noinspection ConstantConditions
        return sender instanceof Player ? getPlayer().getName() : "*";
    }

    public String getDisplayName() {
        //noinspection ConstantConditions
        return sender instanceof Player ? getPlayer().getDisplayName() : getSender().getName();
    }
}
