package com.earth2me.essentials.commands;

import com.earth2me.essentials.RandomTeleport;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.earth2me.essentials.I18n.tl;


public class Commandsettpr extends EssentialsCommand {
    public Commandsettpr() {
        super("settpr");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        RandomTeleport randomTeleport = ess.getRandomTeleport();
        boolean perWorld = randomTeleport.getPerWorld();
        if (perWorld && args.length < 1) {
            throw new Exception(tl("invalidWorld"));
        }
        World world = perWorld ? Bukkit.getWorld(args[0]) : user.getWorld();
        if (world == null && !"perworld".equalsIgnoreCase(args[0])) {
            throw new Exception(tl("invalidWorld"));
        }
        randomTeleport.getCachedLocations(randomTeleport.getCenter(world), randomTeleport.getMinRange(world), randomTeleport.getMaxRange(world)).clear();
        int argOffset = perWorld ? 1 : 0;
        if (args.length == 2 && "perworld".equalsIgnoreCase(args[0])) {
            randomTeleport.setPerWorld(Boolean.parseBoolean(args[1]));
            user.sendMessage(tl("settprValue", args[0].toLowerCase(), args[1].toLowerCase()));
        } else if (args.length == argOffset || "center".equalsIgnoreCase(args[argOffset])) {
            randomTeleport.setCenter(world, user.getLocation());
            user.sendMessage(tl("settpr"));
        } else if (args.length > argOffset + 1) {
            if ("minrange".equalsIgnoreCase(args[argOffset])) {
                randomTeleport.setMinRange(world, Double.parseDouble(args[argOffset + 1]));
            } else if ("maxrange".equalsIgnoreCase(args[argOffset])) {
                randomTeleport.setMaxRange(world, Double.parseDouble(args[argOffset + 1]));
            }
            user.sendMessage(tl("settprValue", args[argOffset].toLowerCase(), args[argOffset + 1].toLowerCase()));
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        boolean perWorld = ess.getRandomTeleport().getPerWorld();
        int argOffset = perWorld ? 1 : 0;
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("perworld");
        }
        if (args.length == 2 && "perworld".equalsIgnoreCase(args[0])) {
            suggestions.addAll(Arrays.asList("true", "false"));
        } else if (args.length == argOffset) {
            suggestions.addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
        } else if (args.length == argOffset + 1) {
            suggestions.addAll(Arrays.asList("center", "minrange", "maxrange"));
        }
        return suggestions;
    }
}
