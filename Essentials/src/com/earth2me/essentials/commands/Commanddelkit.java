package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;

import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;

import static com.earth2me.essentials.I18n.tl;

public class Commanddelkit extends EssentialsCommand {

    public Commanddelkit() {
        super("deletekit");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length != 1) {
            throw new NotEnoughArgumentsException();
        }

        String kitname = args[0];
        if (!ess.getSettings().isPastebinCreateKit()) {
            ess.getKits().removeKit(kitname);
            user.sendMessage(tl("deletedKit", kitname));
        } else {
            user.sendMessage(tl("pastebinDeleteKit"));
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (args.length == 1) {
            try {
                return new ArrayList<>(ess.getKits().getKeys(false));
            } catch (Exception e) {
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }
}
