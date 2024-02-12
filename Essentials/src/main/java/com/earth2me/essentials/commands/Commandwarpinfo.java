package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import org.bukkit.Location;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Commandwarpinfo extends EssentialsCommand {

    public Commandwarpinfo() {
        super("warpinfo");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }
        final String name = args[0];
        final Location loc = ess.getWarps().getWarp(name);
        sender.sendTl("warpInfo", name);
        sender.sendTl("whoisLocation", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            if (ess.getSettings().getPerWarpPermission() && sender.isPlayer()) {
                final List<String> list = new ArrayList<>();
                for (String curWarp : ess.getWarps().getList()) {
                    if (sender.isAuthorized("essentials.warps." + curWarp)) {
                        list.add(curWarp);
                    }
                }
                return list;
            }
            return new ArrayList<>(ess.getWarps().getList());
        } else {
            return Collections.emptyList();
        }
    }
}
