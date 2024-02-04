package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.BanList;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tlLiteral;

public class Commandtempbanip extends EssentialsCommand {
    public Commandtempbanip() {
        super("tempbanip");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;
        final String senderDisplayName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.DISPLAY_NAME;

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

        final String time = getFinalArg(args, 1);
        final long banTimestamp = DateUtil.parseDateDiff(time, true);
        String banReason = DateUtil.removeTimePattern(time);

        final long maxBanLength = ess.getSettings().getMaxTempban() * 1000;
        if (maxBanLength > 0 && ((banTimestamp - GregorianCalendar.getInstance().getTimeInMillis()) > maxBanLength) && sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.tempban.unlimited")) {
            sender.sendTl("oversizedTempban");
            return;
        }

        if (banReason.length() < 2) {
            banReason = tlLiteral("defaultBanReason");
        }

        ess.getServer().getBanList(BanList.Type.IP).addBan(ipAddress, banReason, new Date(banTimestamp), senderName);

        final String banDisplay = tlLiteral("banFormat", banReason, senderDisplayName);
        for (final Player player : ess.getServer().getOnlinePlayers()) {
            if (player.getAddress().getAddress().getHostAddress().equalsIgnoreCase(ipAddress)) {
                player.kickPlayer(banDisplay);
            }
        }

        final String tlKey = "playerTempBanIpAddress";
        final Object[] objects = {senderDisplayName, ipAddress, banReason, DateUtil.formatDateDiff(banTimestamp), banReason};
        ess.getLogger().log(Level.INFO, AdventureUtil.miniToLegacy(tlLiteral(tlKey, objects)));
        ess.broadcastTl(null, "essentials.banip.notify", tlKey, objects);
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            // TODO: Also list IP addresses?
            return getPlayers(server, sender);
        } else {
            // Note: following args are both date diffs _and_ messages; ideally we'd mix with the vanilla handler
            return COMMON_DATE_DIFFS;
        }
    }
}
