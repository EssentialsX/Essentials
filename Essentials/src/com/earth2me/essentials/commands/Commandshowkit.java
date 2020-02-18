package com.earth2me.essentials.commands;

import com.earth2me.essentials.Kit;
import com.earth2me.essentials.User;
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

        final String[] kits = args[0].toLowerCase(Locale.ENGLISH).split(",");
        for (final String kitName : kits) {
            Kit kit = new Kit(kitName, ess);
            user.sendMessage(tl("kitContains", kitName));
            for (String s : kit.getItems()) {
                int maxStringLength = 384;

                if (s.length() > maxStringLength) {
                   // To prevent performance loss, don't display item metadata if it's too long
                    // Keep item name and amount, remove the metadata, and inform the user
                    s = s.substring(0, s.indexOf(" ", s.indexOf(" ") + 1)) + " (item metadata is too long to display)";
                }
                user.sendMessage(tl("kitItem", s));
            }
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(ess.getKits().getKits().getKeys(false)); // TODO: Move this to its own method
        } else {
            return Collections.emptyList();
        }
    }
}
