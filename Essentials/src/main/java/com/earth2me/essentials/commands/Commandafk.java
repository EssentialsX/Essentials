package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandafk extends EssentialsCommand {
    public Commandafk() {
        super("afk");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length > 0 && user.isAuthorized("essentials.afk.others")) {
            User afkUser = user; // if no player found, but message specified, set command executor to target user
            String message;
            try {
                afkUser = getPlayer(server, user, args, 0);
                message = args.length > 1 ? getFinalArg(args, 1) : null;
            } catch (final PlayerNotFoundException e) {
                message = getFinalArg(args, 0);
            }
            toggleAfk(user, afkUser, message);
        } else {
            final String message = args.length > 0 ? getFinalArg(args, 0) : null;
            toggleAfk(user, user, message);
        }
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length > 0) {
            final User afkUser = getPlayer(server, args, 0, true, false);
            final String message = args.length > 1 ? getFinalArg(args, 1) : null;
            toggleAfk(null, afkUser, message);
        } else {
            throw new NotEnoughArgumentsException();
        }
    }

    private void toggleAfk(final User sender, final User user, final String message) throws Exception {
        if (message != null && sender != null) {
            if (sender.isMuted()) {
                final String dateDiff = sender.getMuteTimeout() > 0 ? DateUtil.formatDateDiff(sender.getMuteTimeout()) : null;
                if (dateDiff == null) {
                    throw new Exception(sender.hasMuteReason() ? tl("voiceSilencedReason", sender.getMuteReason()) : tl("voiceSilenced"));
                }
                throw new Exception(sender.hasMuteReason() ? tl("voiceSilencedReasonTime", dateDiff, sender.getMuteReason()) : tl("voiceSilencedTime", dateDiff));
            }
            if (!sender.isAuthorized("essentials.afk.message")) {
                throw new Exception(tl("noPermToAFKMessage"));
            }
        }
        user.setDisplayNick();
        String msg = "";
        String selfmsg = "";

        final boolean currentStatus = user.isAfk();
        final boolean afterStatus = user.toggleAfk(AfkStatusChangeEvent.Cause.COMMAND);
        if (currentStatus == afterStatus) {
            return;
        }

        if (!afterStatus) {
            if (!user.isHidden()) {
                msg = tl("userIsNotAway", user.getDisplayName());
                selfmsg = tl("userIsNotAwaySelf", user.getDisplayName());
            }
            user.updateActivity(false, AfkStatusChangeEvent.Cause.COMMAND);
        } else {
            if (!user.isHidden()) {
                if (message != null) {
                    msg = tl("userIsAwayWithMessage", user.getDisplayName(), message);
                    selfmsg = tl("userIsAwaySelfWithMessage", user.getDisplayName(), message);
                } else {
                    msg = tl("userIsAway", user.getDisplayName());
                    selfmsg = tl("userIsAwaySelf", user.getDisplayName());
                }
            }
            user.setAfkMessage(message);
        }
        if (!msg.isEmpty() && ess.getSettings().broadcastAfkMessage()) {
            // exclude user from receiving general AFK announcement in favor of personal message
            ess.broadcastMessage(user, msg, u -> u == user);
        }
        if (!selfmsg.isEmpty()) {
            user.sendMessage(selfmsg);
        }
        user.setDisplayNick(); // Set this again after toggling
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1 && sender.isAuthorized("essentials.afk.others", ess)) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}

