package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
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
    public void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        if (args.length > 0 && user.isAuthorized("essentials.afk.others")) {
            User afkUser = user; // if no player found, but message specified, set command executor to target user
            String message;
            try {
                afkUser = getPlayer(server, user, args, 0);
                message = args.length > 1 ? getFinalArg(args, 1) : null;
            } catch (PlayerNotFoundException e) {
                message = getFinalArg(args, 0);
            }
            toggleAfk(user, afkUser, message);
        } else {
            String message = args.length > 0 ? getFinalArg(args, 0) : null;
            toggleAfk(user, user, message);
        }
    }

    @Override
    public void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        if (args.length > 0) {
            User afkUser = getPlayer(server, args, 0, true, false);
            String message = args.length > 1 ? getFinalArg(args, 1) : null;
            toggleAfk(null, afkUser, message);
        } else {
            throw new NotEnoughArgumentsException();
        }
    }

    private void toggleAfk(User sender, User user, String message) throws Exception {
        if (message != null && sender != null) {
            if (sender.isMuted()) {
                throw new Exception(sender.hasMuteReason() ? tl("voiceSilencedReason", sender.getMuteReason()) : tl("voiceSilenced"));
            }
            if (!sender.isAuthorized("essentials.afk.message")) {
                throw new Exception(tl("noPermToAFKMessage"));
            }
        }
        user.setDisplayNick();
        String msg = "";
        if (!user.toggleAfk(AfkStatusChangeEvent.Cause.COMMAND)) {
            //user.sendMessage(_("markedAsNotAway"));
            if (!user.isHidden()) {
                msg = tl("userIsNotAway", user.getDisplayName());
            }
            user.updateActivity(false, AfkStatusChangeEvent.Cause.COMMAND);
        } else {
            //user.sendMessage(_("markedAsAway"));
            if (!user.isHidden()) {
                if (message != null) {
                    msg = tl("userIsAwayWithMessage", user.getDisplayName(), message);
                } else {
                    msg = tl("userIsAway", user.getDisplayName());
                }
            }
            user.setAfkMessage(message);
        }
        if (!msg.isEmpty()) {
            ess.broadcastMessage(user, msg);
        }
        user.setDisplayNick(); // Set this again after toggling
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (args.length == 1 && user.isAuthorized("essentials.afk.others")) {
            return getPlayers(server, user);
        } else {
            return Collections.emptyList();
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

