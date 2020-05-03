package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import org.bukkit.BanList;
import org.bukkit.Server;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;


public class Commandtempban extends EssentialsCommand {
    public Commandtempban() {
        super("tempban");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }
        final User user = getPlayer(server, args, 0, true, true);
        if (!user.getBase().isOnline()) {
            if (sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.tempban.offline")) {
                sender.sendMessage(tl("tempbanExemptOffline"));
                return;
            }
        } else {
            if (user.isAuthorized("essentials.tempban.exempt") && sender.isPlayer()) {
                sender.sendMessage(tl("tempbanExempt"));
                return;
            }
        }
        final String time = getFinalArg(args, 1);
        final long banTimestamp = DateUtil.parseDateDiff(time, true);
        String banReason = DateUtil.removeTimePattern(time);

        final long maxBanLength = ess.getSettings().getMaxTempban() * 1000;
        if (maxBanLength > 0 && ((banTimestamp - GregorianCalendar.getInstance().getTimeInMillis()) > maxBanLength) && sender.isPlayer() && !(ess.getUser(sender.getPlayer()).isAuthorized("essentials.tempban.unlimited"))) {
            sender.sendMessage(tl("oversizedTempban"));
            throw new NoChargeException();
        }

        if (banReason.length() < 2) {
            banReason = tl("defaultBanReason");
        }

        final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;
        ess.getServer().getBanList(BanList.Type.NAME).addBan(user.getName(), banReason, new Date(banTimestamp), senderName);
        final String expiry = DateUtil.formatDateDiff(banTimestamp);

        final String banDisplay = tl("tempBanned", expiry, senderName, banReason);
        user.getBase().kickPlayer(banDisplay);

        final String message = tl("playerTempBanned", senderName, user.getName(), expiry, banReason);
        server.getLogger().log(Level.INFO, message);
        ess.broadcastMessage("essentials.ban.notify", message);
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            // Note: following args are both date diffs _and_ messages; ideally we'd mix with the vanilla handler
            return COMMON_DATE_DIFFS;
        }
    }
}
