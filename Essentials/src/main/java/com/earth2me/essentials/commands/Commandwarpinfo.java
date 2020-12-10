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
        if (!sender.isAuthorized("essentials.warpinfo")) {
            throw new Exception(tl("noPerm", "essentials.warpinfo"));
        }
        final String name = args[0];
        final Location loc = ess.getWarps().getWarp(args[0]);
        sender.sendMessage(tl("warpInfo", name));
        sender.sendMessage(tl("whoisLocation", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
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
