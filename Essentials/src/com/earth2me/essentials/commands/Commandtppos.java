package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import static com.earth2me.essentials.I18n.tl;


public class Commandtppos extends EssentialsCommand {
    public Commandtppos() {
        super("tppos");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 3) {
            throw new NotEnoughArgumentsException();
        }

        final double x = args[0].startsWith("~") ? user.getLocation().getX() + Integer.parseInt(args[0].substring(1)) : Integer.parseInt(args[0]);
        final double y = args[1].startsWith("~") ? user.getLocation().getY() + Integer.parseInt(args[1].substring(1)) : Integer.parseInt(args[1]);
        final double z = args[2].startsWith("~") ? user.getLocation().getZ() + Integer.parseInt(args[2].substring(1)) : Integer.parseInt(args[2]);
        final Location loc = new Location(user.getWorld(), x, y, z, user.getLocation().getYaw(), user.getLocation().getPitch());
        if (args.length > 3) {
            loc.setYaw((Float.parseFloat(args[3]) + 180 + 360) % 360);
        }
        if (args.length > 4) {
            loc.setPitch(Float.parseFloat(args[4]));
        }
        if (x > 30000000 || y > 30000000 || z > 30000000 || x < -30000000 || y < -30000000 || z < -30000000) {
            throw new NotEnoughArgumentsException(tl("teleportInvalidLocation"));
        }
        final Trade charge = new Trade(this.getName(), ess);
        charge.isAffordableFor(user);
        user.sendMessage(tl("teleporting", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        user.getTeleport().teleport(loc, charge, TeleportCause.COMMAND);
        throw new NoChargeException();
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 4) {
            throw new NotEnoughArgumentsException();
        }

        User user = getPlayer(server, args, 0, true, false);
        final double x = args[1].startsWith("~") ? user.getLocation().getX() + Integer.parseInt(args[1].substring(1)) : Integer.parseInt(args[1]);
        final double y = args[2].startsWith("~") ? user.getLocation().getY() + Integer.parseInt(args[2].substring(1)) : Integer.parseInt(args[2]);
        final double z = args[3].startsWith("~") ? user.getLocation().getZ() + Integer.parseInt(args[3].substring(1)) : Integer.parseInt(args[3]);
        final Location loc = new Location(user.getWorld(), x, y, z, user.getLocation().getYaw(), user.getLocation().getPitch());
        if (args.length > 4) {
            loc.setYaw((Float.parseFloat(args[4]) + 180 + 360) % 360);
        }
        if (args.length > 5) {
            loc.setPitch(Float.parseFloat(args[5]));
        }
        if (x > 30000000 || y > 30000000 || z > 30000000 || x < -30000000 || y < -30000000 || z < -30000000) {
            throw new NotEnoughArgumentsException(tl("teleportInvalidLocation"));
        }
        sender.sendMessage(tl("teleporting", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        user.sendMessage(tl("teleporting", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        user.getTeleport().teleport(loc, null, TeleportCause.COMMAND);

    }
}
