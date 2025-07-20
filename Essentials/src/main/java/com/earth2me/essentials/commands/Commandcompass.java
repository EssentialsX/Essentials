package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

public class Commandcompass extends EssentialsCommand {
    public Commandcompass() {
        super("compass");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final int bearing = (int) (user.getLocation().getYaw() + 180 + 360) % 360;
        final String dir;
        if (bearing < 23) {
            dir = user.playerTl("north");
        } else if (bearing < 68) {
            dir = user.playerTl("northEast");
        } else if (bearing < 113) {
            dir = user.playerTl("east");
        } else if (bearing < 158) {
            dir = user.playerTl("southEast");
        } else if (bearing < 203) {
            dir = user.playerTl("south");
        } else if (bearing < 248) {
            dir = user.playerTl("southWest");
        } else if (bearing < 293) {
            dir = user.playerTl("west");
        } else if (bearing < 338) {
            dir = user.playerTl("northWest");
        } else {
            dir = user.playerTl("north");
        }
        user.sendTl("compassBearing", dir, bearing);
    }
}
