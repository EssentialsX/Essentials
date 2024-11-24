package com.earth2me.essentials.commands;

import com.earth2me.essentials.Kit;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Commandshowkit extends EssentialsCommand {
    public Commandshowkit() {
        super("showkit");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length != 1) {
            throw new NotEnoughArgumentsException();
        }

        for (final String kitName : args[0].toLowerCase(Locale.ENGLISH).split(",")) {
            user.sendTl("kitContains", kitName);
            for (final String s : new Kit(kitName, ess).getItems()) {
                user.sendTl("kitItem", s);
            }
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(ess.getKits().getKitKeys()); // TODO: Move this to its own method
        } else {
            return Collections.emptyList();
        }
    }
}
