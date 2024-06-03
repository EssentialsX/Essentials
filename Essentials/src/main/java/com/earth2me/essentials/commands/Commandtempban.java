package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.BanList;
import org.bukkit.Server;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tlLiteral;

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
        if (!user.getBase().isOnline() && sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.tempban.offline")) {
            sender.sendTl("tempbanExemptOffline");
            return;
        } else if (user.isAuthorized("essentials.tempban.exempt") && sender.isPlayer()) {
            sender.sendTl("tempbanExempt");
            return;
        }
        final String time = getFinalArg(args, 1);
        final long banTimestamp = DateUtil.parseDateDiff(time, true);
        String banReason = FormatUtil.replaceFormat(DateUtil.removeTimePattern(time));

        final long maxBanLength = ess.getSettings().getMaxTempban() * 1000;
        if (maxBanLength > 0 && ((banTimestamp - GregorianCalendar.getInstance().getTimeInMillis()) > maxBanLength) && sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.tempban.unlimited")) {
            sender.sendTl("oversizedTempban");
            return;
        }

        if (banReason.length() < 2) {
            banReason = tlLiteral("defaultBanReason");
        }

        final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;
        final String senderDisplayName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.DISPLAY_NAME;
        ess.getServer().getBanList(BanList.Type.NAME).addBan(user.getName(), banReason, new Date(banTimestamp), senderName);
        final String expiry = DateUtil.formatDateDiff(banTimestamp);

        final String banDisplay = user.playerTl("tempBanned", expiry, senderDisplayName, banReason);
        user.getBase().kickPlayer(AdventureUtil.miniToLegacy(banDisplay));

        ess.getLogger().log(Level.INFO, AdventureUtil.miniToLegacy(tlLiteral("playerTempBanned", senderDisplayName, user.getName(), expiry, banReason)));
        ess.broadcastTl((IUser) null, "essentials.ban.notify", "playerTempBanned", senderDisplayName, user.getName(), expiry, banReason);
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            // Note: following args are both date diffs _and_ messages; ideally we'd mix with the vanilla handler
            return COMMON_DATE_DIFFS;
        }
    }
}
