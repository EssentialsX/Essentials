package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import net.ess3.api.IUser;
import org.bukkit.Location;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.earth2me.essentials.I18n.tl;

public class Commandwarpinfo extends EssentialsCommand {

    public Commandwarpinfo() {
        super("warpinfo");
    }


    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }
        final String warpName = args[0];
        final Location warpLocation = ess.getWarps().getWarp(args[0]);
        showWarpInfoMessage(sender, warpName, warpLocation);
    }

    private List<String> getAvailableWarpsFor(final IUser user) {
        if (ess.getSettings().getPerWarpPermission() && user != null) {
            return ess.getWarps().getList().stream()
                .filter(warpName -> user.isAuthorized("essentials.warps." + warpName))
                .collect(Collectors.toList());
        }

        return new ArrayList<>(ess.getWarps().getList());
    }

    /**
     * Show the information for the warp to appear
     * @param name the name of the warp
     * @param loc the location of the warp
     */
    private void showWarpInfoMessage(final CommandSource sender, final String name, final Location loc) {
        sender.sendMessage(tl("warpInfo", name));
        sender.sendMessage(tl("whoisLocation", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1 && user.isAuthorized("essentials.warp.list")) {
            return getAvailableWarpsFor(user);
        } else {
            return Collections.emptyList();
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
