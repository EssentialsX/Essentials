package com.earth2me.essentials.commands.essentials;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.commands.EssentialsTreeNode;

import java.util.Map;

public class CommandMapCommand extends EssentialsTreeNode {
    public CommandMapCommand() {
        super("cmd", "commands");
    }

    @Override
    protected void run(CommandSource sender, String commandLabel, String[] args) throws Exception {
        if (ess.getAlternativeCommandsHandler().disabledCommands().size() == 0) {
            sender.sendTl("blockListEmpty");
            return;
        }

        sender.sendTl("blockList");
        for (final Map.Entry<String, String> entry : ess.getAlternativeCommandsHandler().disabledCommands().entrySet()) {
            sender.sendMessage(entry.getKey() + " => " + entry.getValue());
        }
    }
}
