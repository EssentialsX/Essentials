package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;

public class Commandenderchest extends EssentialsCommand {
    public Commandenderchest() {
        super("enderchest");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        User target = user;
        if (args.length > 0 && user.isAuthorized("essentials.enderchest.others")) {
            target = getPlayer(server, user, args, 0);
        }

        user.getBase().closeInventory();
        user.getBase().openInventory(target.getBase().getEnderChest());
        user.setEnderSee(!target.equals(user));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1 && user.isAuthorized("essentials.enderchest.others")) {
            return getPlayers(server, user);
        } else {
            return Collections.emptyList();
        }
    }
}
