package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;
import org.bukkit.BanList;

public class Commandwarn extends EssentialsCommand {
    public Commandwarn() {
        super("warn");
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
        } catch (PlayerNotFoundException e) {
            nomatch = true;
            user = ess.getUser(new OfflinePlayer(args[0], ess.getServer()));
        }
        if (!user.getBase().isOnline()) {
            if (sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.warn.offline")) {
                throw new Exception(tl("warnExemptOffline"));
            }
        } else if (user.isAuthorized("essentials.warn.exempt") && sender.isPlayer()) {
            throw new Exception(tl("warnExempt"));
        }

        final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;
        final String warnReason;
        if (args.length > 1) {
            warnReason = FormatUtil.replaceFormat(getFinalArg(args, 1).replace("\\n", "\n").replace("|", "\n"));
        } else {
            warnReason = tl("defaultWarnReason", user.getWarnings());
        }
        
        user.addWarning(warnReason, senderName);
        final String warnDisplay = tl("warnFormat", warnReason, senderName, user.getWarnings());

        user.sendMessage(warnDisplay);
        
        server.getLogger().log(Level.INFO, tl("playerWarned", senderName, user.getName(), warnDisplay, user.getWarnings()));

        if (nomatch) {
            sender.sendMessage(tl("userUnknown", user.getName()));
        }
        
        ess.broadcastMessage("essentials.warn.notify", tl("playerWarned", senderName, user.getName(), warnReason, user.getWarnings()));
        
        if (user.getWarnings() > ess.getConfig().getInt("max-warnings")) {
            final String finalWarning = tl("warnFinal", warnReason);
            
            if (ess.getConfig().getBoolean("ban-on-final-warning")) {
                ess.getServer().getBanList(BanList.Type.NAME).addBan(user.getName(), finalWarning, null, senderName);
                server.getLogger().log(Level.INFO, tl("playerBanned", senderName, user.getName(), finalWarning));
            }
            
            user.getBase().kickPlayer(finalWarning);
        } 
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
