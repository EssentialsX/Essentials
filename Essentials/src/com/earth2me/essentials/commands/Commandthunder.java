package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.google.common.collect.Lists;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;


/**
 * <p>Commandthunder class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class Commandthunder extends EssentialsCommand {
    /**
     * <p>Constructor for Commandthunder.</p>
     */
    public Commandthunder() {
        super("thunder");
    }

    /** {@inheritDoc} */
    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final World world = user.getWorld();
        final boolean setThunder = args[0].equalsIgnoreCase("true");
        if (args.length > 1) {

            world.setThundering(setThunder);
            world.setThunderDuration(Integer.parseInt(args[1]) * 20);
            user.sendMessage(tl("thunderDuration", (setThunder ? tl("enabled") : tl("disabled")), Integer.parseInt(args[1])));

        } else {
            world.setThundering(setThunder);
            user.sendMessage(tl("thunder", setThunder ? tl("enabled") : tl("disabled")));
        }
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("true", "false");
        } else if (args.length == 2) {
            return COMMON_DATE_DIFFS;
        } else {
            return Collections.emptyList();
        }
    }
}
