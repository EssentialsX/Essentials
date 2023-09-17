package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.CommonPlaceholders;
import net.ess3.api.IUser;
import net.ess3.api.TranslatableException;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            throw new TranslatableException("kitNotFound");
        }

        User target = user;
        if (args.length > 1 && user.isAuthorized("essentials.kitreset.others")) {
            target = getPlayer(server, user, args, 1);
        }

        target.setKitTimestamp(kitName, 0);
        if (user.equals(target)) {
            user.sendTl("kitReset", kitName);
        } else {
            user.sendTl("kitResetOther", kitName, CommonPlaceholders.displayName((IUser) target));
        }
    }

    @Override
    protected void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        final String kitName = args[0];
        if (ess.getKits().getKit(kitName) == null) {
            throw new TranslatableException("kitNotFound");
        }

        final User target = getPlayer(server, sender, args, 1);
        target.setKitTimestamp(kitName, 0);
        sender.sendTl("kitResetOther", kitName, CommonPlaceholders.displayName((IUser) target));
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(ess.getKits().getKitKeys());
        } else if (args.length == 2 && sender.isAuthorized("essentials.kitreset.others")) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
