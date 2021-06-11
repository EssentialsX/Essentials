package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.config.ConfigurateUtil;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Override
    protected void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        final String kitName = args[0];
        if (ess.getKits().getKit(kitName) == null) {
            throw new Exception(tl("kitNotFound"));
        }

        final User target = getPlayer(server, sender, args, 1);
        target.setKitTimestamp(kitName, 0);
        sender.sendMessage(tl("kitResetOther", kitName, target.getDisplayName()));
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(ConfigurateUtil.getKeys(ess.getKits().getKits()));
        } else if (args.length == 2 && sender.isAuthorized("essentials.kitreset.others", ess)) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
