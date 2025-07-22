package com.earth2me.essentials.commands.essentials;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.commands.EssentialsTreeNode;

public class ReloadCommand extends EssentialsTreeNode {

    public ReloadCommand() {
        super("reload");
    }

    @Override
    protected void run(final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        ess.reload();
        sender.sendTl("essentialsReload", ess.getDescription().getVersion());
    }
}
