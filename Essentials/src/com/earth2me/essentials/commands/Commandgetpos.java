package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandgetpos extends EssentialsCommand {
    public Commandgetpos() {
        super("getpos");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length > 0 && user.isAuthorized("essentials.getpos.others")) {
            final User otherUser = getPlayer(server, user, args, 0);
            outputPosition(user.getSource(), otherUser.getLocation(), user.getLocation());
            return;
        }
        outputPosition(user.getSource(), user.getLocation(), null);
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        final User user = getPlayer(server, args, 0, true, false);
        outputPosition(sender, user.getLocation(), null);
    }

    private void outputPosition(final CommandSource sender, final Location coords, final Location distance) {
        sender.sendMessage(tl("currentWorld", coords.getWorld().getName()));
        sender.sendMessage(tl("posX", coords.getBlockX()));
        sender.sendMessage(tl("posY", coords.getBlockY()));
        sender.sendMessage(tl("posZ", coords.getBlockZ()));
        sender.sendMessage(tl("posYaw", (coords.getYaw() + 360) % 360));
        sender.sendMessage(tl("posPitch", coords.getPitch()));
        if (distance != null && coords.getWorld().equals(distance.getWorld())) {
            sender.sendMessage(tl("distance", coords.distance(distance)));
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1 && sender.isAuthorized("essentials.getpos.others", ess)) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
