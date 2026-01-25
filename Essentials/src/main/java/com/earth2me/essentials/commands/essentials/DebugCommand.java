package com.earth2me.essentials.commands.essentials;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.commands.EssentialsTreeNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DebugCommand extends EssentialsTreeNode {
    public DebugCommand() {
        super("debug", "verbose");
    }

    @Override
    protected void run(CommandSource sender, String commandLabel, String[] args) throws Exception {
        final boolean newDebugState;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("true")) {
                newDebugState = true;
            } else if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("false")) {
                newDebugState = false;
            } else {
                newDebugState = !ess.getSettings().isDebug();
            }
        } else {
            newDebugState = !ess.getSettings().isDebug();
        }

        ess.getSettings().setDebug(newDebugState);
        sender.sendMessage("Essentials " + ess.getDescription().getVersion() + " debug mode " + (ess.getSettings().isDebug() ? "enabled" : "disabled"));
    }

    @Override
    protected List<String> tabComplete(CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("on", "off");
        }
        return Collections.emptyList();
    }
}
