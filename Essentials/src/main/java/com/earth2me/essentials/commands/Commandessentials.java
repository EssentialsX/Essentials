package com.earth2me.essentials.commands;

import com.earth2me.essentials.commands.essentials.CleanupCommand;
import com.earth2me.essentials.commands.essentials.CommandMapCommand;
import com.earth2me.essentials.commands.essentials.DebugCommand;
import com.earth2me.essentials.commands.essentials.DumpCommand;
import com.earth2me.essentials.commands.essentials.HomesCommand;
import com.earth2me.essentials.commands.essentials.ItemTestCommand;
import com.earth2me.essentials.commands.essentials.MooCommand;
import com.earth2me.essentials.commands.essentials.NyanCommand;
import com.earth2me.essentials.commands.essentials.ReloadCommand;
import com.earth2me.essentials.commands.essentials.UsermapCommand;
import com.earth2me.essentials.commands.essentials.VersionCommand;

// This command has 4 undocumented behaviours #EasterEgg
public class Commandessentials extends EssentialsTreeCommand {
    public Commandessentials() {
        super("essentials");
        registerNode(new VersionCommand());
        registerNode(new DebugCommand());
        registerNode(new CommandMapCommand());
        registerNode(new DumpCommand());
        registerNode(new ReloadCommand());
        registerNode(new CleanupCommand());
        registerNode(new HomesCommand());
        registerNode(new UsermapCommand());
        registerNode(new ItemTestCommand());
        registerNode(new NyanCommand());
        registerNode(new MooCommand());
    }
}
