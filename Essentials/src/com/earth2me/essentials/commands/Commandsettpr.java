package com.earth2me.essentials.commands;

import com.earth2me.essentials.RandomTeleport;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;


public class Commandsettpr extends EssentialsCommand {
    public Commandsettpr() {
        super("settpr");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        RandomTeleport randomTeleport = ess.getRandomTeleport();
        World world = user.getWorld();
        randomTeleport.getCachedLocations(randomTeleport.getCenter(world), randomTeleport.getMinRange(world), randomTeleport.getMaxRange(world)).clear();
        if (args.length == 0 || "center".equalsIgnoreCase(args[0])) {
            randomTeleport.setCenter(world, user.getLocation());
            user.sendMessage(tl("settpr"));
        } else if (args.length > 1) {
            if ("minrange".equalsIgnoreCase(args[0])) {
                randomTeleport.setMinRange(world, Double.parseDouble(args[1]));
            } else if ("maxrange".equalsIgnoreCase(args[0])) {
                randomTeleport.setMaxRange(world, Double.parseDouble(args[1]));
            } else if ("perworld".equalsIgnoreCase(args[0])) {
                randomTeleport.setPerWorld(Boolean.parseBoolean(args[1]));
            }
            user.sendMessage(tl("settprValue", args[0].toLowerCase(), args[1].toLowerCase()));
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("center", "minrange", "maxrange", "perworld");
        } else if (args.length == 2 && "perworld".equalsIgnoreCase(args[0])) {
            return Arrays.asList("true", "false");
        }
        return Collections.emptyList();
    }
}
