package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Commandnuke extends EssentialsCommand {

    public static final String NUKE_META_KEY = "ess_tnt_proj";

    public Commandnuke() {
        super("nuke");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws NotEnoughArgumentsException, PlayerNotFoundException {
        final Iterable<User> targets;
        if (args.length > 0) {
            targets = new ArrayList<>();
            for (int i = 0; i < args.length; ++i) {
                ((ArrayList<User>) targets).add(getPlayer(server, sender, args, i));
            }
        } else {
            targets = ess.getOnlineUsers();
        }
        for (final User user : targets) {
            if (user == null) {
                continue;
            }
            user.sendTl("nuke");
            final Location loc = user.getLocation();
            final World world = loc.getWorld();
            if (world != null) {
                for (int x = -10; x <= 10; x += 5) {
                    for (int z = -10; z <= 10; z += 5) {
                        final TNTPrimed entity = world.spawn(new Location(world, loc.getBlockX() + x, world.getHighestBlockYAt(loc) + 64, loc.getBlockZ() + z), TNTPrimed.class);
                        entity.setMetadata(NUKE_META_KEY, new FixedMetadataValue(ess, true));
                    }
                }
            }
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
