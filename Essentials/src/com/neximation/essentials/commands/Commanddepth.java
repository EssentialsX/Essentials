package com.neximation.essentials.commands;

import com.neximation.essentials.User;
import org.bukkit.Server;

import static com.neximation.essentials.I18n.tl;


public class Commanddepth extends EssentialsCommand {
    public Commanddepth() {
        super("depth");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final int depth = user.getLocation().getBlockY() - 63;
        if (depth > 0) {
            user.sendMessage(tl("depthAboveSea", depth));
        } else if (depth < 0) {
            user.sendMessage(tl("depthBelowSea", (-depth)));
        } else {
            user.sendMessage(tl("depth"));
        }
    }
}
