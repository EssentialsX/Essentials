package com.earth2me.essentials.commands;

import com.earth2me.essentials.RandomTeleport;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandsettpr extends EssentialsCommand {
    public Commandsettpr() {
        super("settpr");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final RandomTeleport randomTeleport = ess.getRandomTeleport();
        randomTeleport.getCachedLocations().clear();
        if (args.length == 0 || "center".equalsIgnoreCase(args[0])) {
            randomTeleport.setCenter(user.getLocation());
            user.sendMessage(tl("settpr"));
        } else if (args.length > 1) {
            if ("minrange".equalsIgnoreCase(args[0])) {
                randomTeleport.setMinRange(Double.parseDouble(args[1]));
            } else if ("maxrange".equalsIgnoreCase(args[0])) {
                randomTeleport.setMaxRange(Double.parseDouble(args[1]));
            }
            user.sendMessage(tl("settprValue", args[0].toLowerCase(), args[1].toLowerCase()));
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return Arrays.asList("center", "minrange", "maxrange");
        }
        return Collections.emptyList();
    }
}
