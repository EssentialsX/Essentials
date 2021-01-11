package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commandkitreset extends EssentialsCommand {
    public Commandkitreset() {
        super("kitreset");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final String kitName = args[0];
        if (ess.getKits().getKit(kitName) == null) {
            throw new Exception(tl("kitNotFound"));
        }

        User target = user;
        if (args.length > 1 && user.isAuthorized("essentials.kitreset.others")) {
            target = getPlayer(server, user, args, 1);
        }

        target.setKitTimestamp(kitName, 0);
        if (user.equals(target)) {
            user.sendMessage(tl("kitReset", kitName));
        } else {
            user.sendMessage(tl("kitResetOther", kitName, target.getDisplayName()));
        }
    }
}
