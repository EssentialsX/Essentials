package com.neximation.essentials.commands;

import com.neximation.essentials.CommandSource;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.neximation.essentials.I18n.tl;


public class Commanddelwarp extends EssentialsCommand {
    public Commanddelwarp() {
        super("delwarp");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        ess.getWarps().removeWarp(args[0]);
        sender.sendMessage(tl("deleteWarp", args[0]));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(ess.getWarps().getList());
        } else {
            return Collections.emptyList();
        }
    }
}
