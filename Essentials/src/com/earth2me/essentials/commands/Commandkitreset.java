package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Kit;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandkitreset extends EssentialsCommand {
    public Commandkitreset() {
        super ("kitreset");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (user.isAuthorized("essentials.kit.reset")) {
            final User userTo = getPlayer(server, user, args, 0);
            final Kit kit = new Kit(args[1], ess);
            kit.resetTime(userTo);
            user.sendMessage(tl("kitReset", userTo.getDisplayName(), kit.getName()));
            userTo.sendMessage(tl("kitResetPlayer", kit.getName()));
        }
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        final User userTo = getPlayer(server, args, 0, true, false);
        final Kit kit = new Kit(args[1], ess);
        kit.resetTime(userTo);
        sender.sendMessage(tl("kitReset", userTo.getDisplayName(), kit.getName()));
        userTo.sendMessage(tl("kitResetPlayer", kit.getName()));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1 && user.isAuthorized("essentials.kit.reset")) {
            return getPlayers(server, user);
<<<<<<< HEAD
        } else if (args.length == 2 && user.isAuthorized("essentials.kit.reset"))  {
=======
        } else if (args.length == 2)  {
>>>>>>> resetkitcommand
            for (String kitName : ess.getKits().getKits().getKeys(false)) {
                if (!user.isAuthorized("essentials.kits." + kitName)) { // Only check perm, not time or money
                    continue;
                }
                options.add(kitName);
            }
            return options;
        }
<<<<<<< HEAD
        return options;
=======
        return Collections.emptyList();
>>>>>>> resetkitcommand
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else if (args.length == 2) {
            return new ArrayList<>(ess.getKits().getKits().getKeys(false));
        } else {
            return Collections.emptyList();
        }
    }
}

