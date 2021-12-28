package com.earth2me.essentials;

import com.earth2me.essentials.utils.AdventureUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.earth2me.essentials.I18n.tlLiteral;

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

    public void sendTl(final IEssentials ess, final String tlKey, final Object... args) {
        if (isPlayer()) {
            //noinspection ConstantConditions
            getUser(ess).sendTl(tlKey, args);
            return;
        }

        final String translation = tlLiteral(tlKey, args);
        if (!translation.startsWith("MM||")) {
            sendMessage(translation);
            return;
        }
        sendComponent(ess, MiniMessage.miniMessage().parse(translation.substring(4)));
    }

    public String tl(final IEssentials ess, final String tlKey, final Object... args) {
        if (isPlayer()) {
            //noinspection ConstantConditions
            return getUser(ess).playerTl(tlKey, args);
        }
        return tlLiteral(tlKey, args);
    }

    public Component tlComponent(final IEssentials ess, final String tlKey, final Object... args) {
        if (isPlayer()) {
            //noinspection ConstantConditions
            return getUser(ess).tlComponent(tlKey, args);
        }
        final String translation = tlLiteral(tlKey, args);
        if (!translation.startsWith("MM||")) {
            return AdventureUtil.toComponent(translation);
        }
        return MiniMessage.miniMessage().parse(translation.substring(4));
    }

    public void sendComponent(final IEssentials ess, final Component component) {
        final BukkitAudiences audiences = ((Essentials) ess).getBukkitAudience();
        audiences.sender(sender).sendMessage(component);
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
        //noinspection ConstantConditions
        return !(sender instanceof Player) || getUser(ess).isAuthorized(permission);
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
