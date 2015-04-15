package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import org.bukkit.Server;

import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;


public class Commandmute extends EssentialsCommand {
    public Commandmute() {
        super("mute");
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
            if (sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.mute.offline")) {
                throw new Exception(tl("muteExemptOffline"));
            }
        } else {
            if (user.isAuthorized("essentials.mute.exempt") && sender.isPlayer()) {
                throw new Exception(tl("muteExempt"));
            }
        }

        long muteTimestamp = 0;

        if (args.length > 1) {
            final String time = getFinalArg(args, 1);
            muteTimestamp = DateUtil.parseDateDiff(time, true);
            user.setMuted(true);
        } else {
            user.setMuted(!user.getMuted());
        }
        user.setMuteTimeout(muteTimestamp);
        final boolean muted = user.getMuted();
        String muteTime = DateUtil.formatDateDiff(muteTimestamp);

        if (nomatch) {
            sender.sendMessage(tl("userUnknown", user.getName()));
        }

        if (muted) {
            if (muteTimestamp > 0) {
                sender.sendMessage(tl("mutedPlayerFor", user.getDisplayName(), muteTime));
                user.sendMessage(tl("playerMutedFor", muteTime));
            } else {
                sender.sendMessage(tl("mutedPlayer", user.getDisplayName()));
                user.sendMessage(tl("playerMuted"));
            }
            final String message = tl("muteNotify", sender.getSender().getName(), user.getName(), muteTime);
            server.getLogger().log(Level.INFO, message);
            ess.broadcastMessage("essentials.mute.notify", message);
        } else {
            sender.sendMessage(tl("unmutedPlayer", user.getDisplayName()));
            user.sendMessage(tl("playerUnmuted"));
        }
    }
}
