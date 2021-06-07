package com.earth2me.essentials.commands;

import com.earth2me.essentials.Kit;
import com.earth2me.essentials.User;
import com.earth2me.essentials.config.ConfigurateUtil;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.earth2me.essentials.I18n.tl;

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
            user.sendMessage(tl("kitContains", kitName));
            for (final String s : new Kit(kitName, ess).getItems()) {
                user.sendMessage(tl("kitItem", s));
            }
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(ConfigurateUtil.getKeys(ess.getKits().getKits())); // TODO: Move this to its own method
        } else {
            return Collections.emptyList();
        }
    }
}
