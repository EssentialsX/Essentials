package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent;

import static com.earth2me.essentials.I18n.tl;

public class Commanddown extends EssentialsCommand {
    public Commanddown() {
        super("down");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final int topX = user.getLocation().getBlockX();
        final int topZ = user.getLocation().getBlockZ();
        final float pitch = user.getLocation().getPitch();
        final float yaw = user.getLocation().getYaw();

        Location loc = user.getLocation();
        for (int y = user.getLocation().getBlockY() - 3; y > 0; y--) {
            if (!LocationUtil.isBlockUnsafe(user.getWorld(), topX, y, topZ)) {
                loc = new Location(user.getWorld(), topX, y, topZ, yaw, pitch);
                break;
            }
        }

        user.getTeleport().teleport(loc, new Trade(this.getName(), ess), PlayerTeleportEvent.TeleportCause.COMMAND);
        user.sendMessage(tl("teleportDown", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

    }
}
