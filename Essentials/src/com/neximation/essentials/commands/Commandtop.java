package com.neximation.essentials.commands;

import com.neximation.essentials.Trade;
import com.neximation.essentials.User;
import com.neximation.essentials.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import static com.neximation.essentials.I18n.tl;


public class Commandtop extends EssentialsCommand {
    public Commandtop() {
        super("top");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final int topX = user.getLocation().getBlockX();
        final int topZ = user.getLocation().getBlockZ();
        final float pitch = user.getLocation().getPitch();
        final float yaw = user.getLocation().getYaw();
        final Location loc = LocationUtil.getSafeDestination(new Location(user.getWorld(), topX, user.getWorld().getMaxHeight(), topZ, yaw, pitch));
        user.getTeleport().teleport(loc, new Trade(this.getName(), ess), TeleportCause.COMMAND);
        user.sendMessage(tl("teleportTop", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

    }
}
