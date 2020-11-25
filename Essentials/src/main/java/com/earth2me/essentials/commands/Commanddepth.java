package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commanddepth extends EssentialsCommand {
    public Commanddepth() {
        super("depth");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final int depth = user.getLocation().getBlockY() - user.getWorld().getSeaLevel();
        if (depth > 0) {
            user.sendMessage(tl("depthAboveSea", depth));
        } else if (depth < 0) {
            user.sendMessage(tl("depthBelowSea", -depth));
        } else {
            user.sendMessage(tl("depth"));
        }
    }
}
