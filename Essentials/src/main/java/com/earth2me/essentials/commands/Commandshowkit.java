package com.earth2me.essentials.commands;

import com.earth2me.essentials.Kit;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
            final Kit kit = new Kit(kitName, ess);
            for (final String s : kit.getBasicItems()) {
                user.sendTl("kitItem", s);
            }
            for (final String s : kit.getGearItems().stream().filter(is -> is != null).collect(Collectors.toList())) {
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
