package com.earth2me.essentials.commands.essentials;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.commands.EssentialsTreeNode;

public class DebugCommand extends EssentialsTreeNode {
    public DebugCommand() {
        super("debug", "verbose");
    }

    @Override
    protected void run(CommandSource sender, String commandLabel, String[] args) throws Exception {
        ess.getSettings().setDebug(!ess.getSettings().isDebug());
        sender.sendMessage("Essentials " + ess.getDescription().getVersion() + " debug mode " + (ess.getSettings().isDebug() ? "enabled" : "disabled"));
    }
}
