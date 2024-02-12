package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import net.ess3.api.TranslatableException;
import net.essentialsx.api.v2.events.WarpModifyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Commanddelwarp extends EssentialsCommand {
    public Commanddelwarp() {
        super("delwarp");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }
        //Check if warp exists before calling the event
        if (ess.getWarps().isWarp(args[0])) {
            Location location;
            try {
                location = ess.getWarps().getWarp(args[0]);
            } catch (Exception ignored) {
                // World is unloaded/deleted
                location = null;
            }
            final WarpModifyEvent event = new WarpModifyEvent(sender.getUser(), args[0], location, null, WarpModifyEvent.WarpModifyCause.DELETE);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            ess.getWarps().removeWarp(args[0]);
            sender.sendTl("deleteWarp", args[0]);
        } else {
            throw new TranslatableException("warpNotExist");
        }
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
