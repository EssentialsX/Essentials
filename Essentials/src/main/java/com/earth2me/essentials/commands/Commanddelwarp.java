package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import net.ess3.api.events.WarpModifyCause;
import net.ess3.api.events.WarpModifyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commanddelwarp extends EssentialsCommand {
    public Commanddelwarp() {
        super("delwarp");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }
        final WarpModifyEvent event = new WarpModifyEvent(sender.getUser(this.ess), args[0] ,WarpModifyCause.DELETE);
        if (event.isCancelled()) {
            return;
        }
        Bukkit.getServer().getPluginManager().callEvent(event);
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
