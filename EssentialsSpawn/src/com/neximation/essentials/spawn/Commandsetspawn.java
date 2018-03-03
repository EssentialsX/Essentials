package com.neximation.essentials.spawn;

import com.neximation.essentials.User;
import com.neximation.essentials.commands.EssentialsCommand;
import org.bukkit.Server;

import static com.neximation.essentials.I18n.tl;


public class Commandsetspawn extends EssentialsCommand {
    public Commandsetspawn() {
        super("setspawn");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final String group = args.length > 0 ? getFinalArg(args, 0) : "default";
        ((SpawnStorage) module).setSpawn(user.getLocation(), group);
        user.sendMessage(tl("spawnSet", group));
    }
}
