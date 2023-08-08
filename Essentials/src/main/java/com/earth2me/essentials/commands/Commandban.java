package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.OfflinePlayerStub;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.BanList;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;

public class Commandban extends EssentialsCommand {
    public Commandban() {
        super("ban");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        boolean nomatch = false;
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        User user;
        try {
            user = getPlayer(server, args, 0, true, true);
        } catch (final PlayerNotFoundException e) {
            nomatch = true;
            user = ess.getUser(new OfflinePlayerStub(args[0], ess.getServer()));
        }
        if (!user.getBase().isOnline()) {
            if (sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.ban.offline")) {
                throw new Exception(tl("banExemptOffline"));
            }
        } else if (user.isAuthorized("essentials.ban.exempt") && sender.isPlayer()) {
            throw new Exception(tl("banExempt"));
        }

        final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;
        final String senderDisplayName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.DISPLAY_NAME;
        final String banReason;
        if (args.length > 1) {
            banReason = FormatUtil.replaceFormat(getFinalArg(args, 1).replace("\\n", "\n").replace("|", "\n"));
        } else {
            banReason = tl("defaultBanReason");
        }

        ess.getServer().getBanList(BanList.Type.NAME).addBan(user.getName(), banReason, null, senderName);

        final String banDisplay = tl("banFormat", banReason, senderDisplayName);

        user.getBase().kickPlayer(banDisplay);
        ess.getLogger().log(Level.INFO, tl("playerBanned", senderDisplayName, user.getName(), banDisplay));

        if (nomatch) {
            sender.sendMessage(tl("userUnknown", user.getName()));
        }

        ess.broadcastMessage("essentials.ban.notify", tl("playerBanned", senderDisplayName, user.getName(), banReason));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
