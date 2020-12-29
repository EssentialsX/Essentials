package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;

import static com.earth2me.essentials.I18n.tl;

public class Commandvanish extends EssentialsToggleCommand {
    public Commandvanish() {
        super("vanish", "essentials.vanish.others");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        toggleOtherPlayers(server, sender, args);
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        handleToggleWithArgs(server, user, args);
    }

    @Override
    void togglePlayer(final CommandSource sender, final User user, Boolean enabled) {
        if (enabled == null) {
            enabled = !user.isVanished();
        }

        final VanishStatusChangeEvent vanishEvent = new VanishStatusChangeEvent(sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null, user, enabled);
        ess.getServer().getPluginManager().callEvent(vanishEvent);
        if (vanishEvent.isCancelled()) {
            return;
        }

        user.setVanished(enabled);
        user.sendMessage(tl("vanish", user.getDisplayName(), enabled ? tl("enabled") : tl("disabled")));

        if (enabled) {
            user.sendMessage(tl("vanished"));
        }

        if (ess.getSettings().isFakeMessageOnVanish()) {
            final Player player = user.getBase();
            String msg;
            if (enabled) {
                if (ess.getSettings().isCustomQuitMessage()) {
                    msg = ess.getSettings().getCustomQuitMessage();
                } else {
                    msg = "&e{USERNAME} left the game";
                }
            } else {
                if (ess.getSettings().isCustomJoinMessage()) {
                    msg = ess.getSettings().getCustomJoinMessage();
                } else {
                    msg = "&e{USERNAME} joined the game";
                }
            }
            msg = msg.replace("{PLAYER}", player.getDisplayName())
                    .replace("{USERNAME}", player.getName())
                    .replace("{ONLINE}", NumberFormat.getInstance().format(ess.getOnlinePlayers().size()))
                    .replace("{UPTIME}", DateUtil.formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime()));
            ess.getServer().broadcastMessage(msg);
        }

        if (!sender.isPlayer() || !sender.getPlayer().equals(user.getBase())) {
            sender.sendMessage(tl("vanish", user.getDisplayName(), enabled ? tl("enabled") : tl("disabled")));
        }
    }
}
