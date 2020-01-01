package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;


public class Commandcompass extends EssentialsCommand {
    public Commandcompass() {
        super("compass");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final int bearing = (int) (user.getLocation().getYaw() + 180 + 360) % 360;
        String dir;
        if (bearing < 23) {
            dir = tl("north");
        } else if (bearing < 68) {
            dir = tl("northEast");
        } else if (bearing < 113) {
            dir = tl("east");
        } else if (bearing < 158) {
            dir = tl("southEast");
        } else if (bearing < 203) {
            dir = tl("south");
        } else if (bearing < 248) {
            dir = tl("southWest");
        } else if (bearing < 293) {
            dir = tl("west");
        } else if (bearing < 338) {
            dir = tl("northWest");
        } else {
            dir = tl("north");
        }
        user.sendMessage(tl("compassBearing", dir, bearing));
    }
}
