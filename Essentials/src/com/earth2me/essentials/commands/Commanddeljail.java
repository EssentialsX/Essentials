package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commanddeljail extends EssentialsCommand {
    public Commanddeljail() {
        super("deljail");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        if (ess.getJails().getJail(args[0]) == null) {
            throw new Exception(tl("jailNotExist"));
        }

        ess.getJails().removeJail(args[0]);
        sender.sendMessage(tl("deleteJail", args[0]));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            try {
                return new ArrayList<>(ess.getJails().getList());
            } catch (final Exception e) {
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }
}
