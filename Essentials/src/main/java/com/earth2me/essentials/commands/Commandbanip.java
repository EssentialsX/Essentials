package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.BanList;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;

public class Commandbanip extends EssentialsCommand {
    public Commandbanip() {
        super("banip");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final String senderName = sender.isPlayer() ? sender.getPlayer().getName() : Console.NAME;
        final String senderDisplayName = sender.isPlayer() ? sender.getPlayer().getName() : Console.DISPLAY_NAME;

        String ipAddress;
        if (FormatUtil.validIP(args[0])) {
            ipAddress = args[0];
        } else {
            try {
                final User player = getPlayer(server, args, 0, true, true);
                ipAddress = player.getLastLoginAddress();
            } catch (final PlayerNotFoundException ex) {
                ipAddress = args[0];
            }
        }

        if (ipAddress.isEmpty()) {
            throw new PlayerNotFoundException();
        }

        final String banReason;
        if (args.length > 1) {
            banReason = FormatUtil.replaceFormat(getFinalArg(args, 1).replace("\\n", "\n").replace("|", "\n"));
        } else {
            banReason = tl("defaultBanReason");
        }

        final String banDisplay = tl("banFormat", banReason, senderDisplayName);

        ess.getServer().getBanList(BanList.Type.IP).addBan(ipAddress, banReason, null, senderName);
        ess.getLogger().log(Level.INFO, tl("playerBanIpAddress", senderDisplayName, ipAddress, banReason));

        for (final Player player : ess.getServer().getOnlinePlayers()) {
            if (player.getAddress().getAddress().getHostAddress().equalsIgnoreCase(ipAddress)) {
                player.kickPlayer(banDisplay);
            }
        }

        ess.broadcastMessage("essentials.banip.notify", tl("playerBanIpAddress", senderDisplayName, ipAddress, banReason));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            // TODO: Also list IP addresses?
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
