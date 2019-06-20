package com.earth2me.essentials.spawn;

import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;


/**
 * <p>Commandsetspawn class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class Commandsetspawn extends EssentialsCommand {
    /**
     * <p>Constructor for Commandsetspawn.</p>
     */
    public Commandsetspawn() {
        super("setspawn");
    }

    /** {@inheritDoc} */
    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) {
        final String group = args.length > 0 ? getFinalArg(args, 0) : "default";
        ((SpawnStorage) module).setSpawn(user.getLocation(), group);
        user.sendMessage(tl("spawnSet", group));
    }
}
