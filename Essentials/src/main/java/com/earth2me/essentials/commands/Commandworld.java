package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandworld extends EssentialsCommand {
    public Commandworld() {
        super("world");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final World world;

        if (args.length < 1) {
            World nether = null;

            final List<World> worlds = server.getWorlds();

            for (final World world2 : worlds) {
                if (world2.getEnvironment() == World.Environment.NETHER) {
                    nether = world2;
                    break;
                }
            }
            if (nether == null) {
                return;
            }
            world = user.getWorld() == nether ? worlds.get(0) : nether;
        } else {
            world = ess.getWorld(getFinalArg(args, 0));
            if (world == null) {
                user.sendMessage(tl("invalidWorld"));
                user.sendMessage(tl("possibleWorlds", server.getWorlds().size() - 1));
                user.sendMessage(tl("typeWorldName"));
                throw new NoChargeException();
            }
        }

        if (ess.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("essentials.worlds." + world.getName())) {
            throw new Exception(tl("noPerm", "essentials.worlds." + world.getName()));
        }

        final double factor;
        if (user.getWorld().getEnvironment() == World.Environment.NETHER && world.getEnvironment() == World.Environment.NORMAL) {
            factor = 8.0;
        } else if (user.getWorld().getEnvironment() == World.Environment.NORMAL && world.getEnvironment() == World.Environment.NETHER) {
            factor = 1.0 / 8.0;
        } else {
            factor = 1.0;
        }

        final Location loc = user.getLocation();
        final Location target = new Location(world, loc.getBlockX() * factor + .5, loc.getBlockY(), loc.getBlockZ() * factor + .5);

        final Trade charge = new Trade(this.getName(), ess);
        charge.isAffordableFor(user);
        user.getAsyncTeleport().teleport(target, charge, TeleportCause.COMMAND, getNewExceptionFuture(user.getSource(), commandLabel));

        throw new NoChargeException();
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            final List<String> worlds = Lists.newArrayList();
            for (final World world : server.getWorlds()) {
                if (ess.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("essentials.worlds." + world.getName())) {
                    continue;
                }
                worlds.add(world.getName());
            }
            return worlds;
        } else {
            return Collections.emptyList();
        }
    }
}
