package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandcreatekit extends EssentialsCommand {

    public Commandcreatekit() {
        super("createkit");
    }

    // /createkit <name> <delay>
    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length != 2) {
            throw new NotEnoughArgumentsException();
        }

        // Command handler will auto fail if this fails.
        long delay = Long.valueOf(args[1]);
        String kitname = args[0];
        ItemStack[] items = user.getBase().getInventory().getContents();
        List<String> list = new ArrayList<String>();
        for (ItemStack is : items) {
            if (is != null && is.getType() != null && is.getType() != Material.AIR) {
                String serialized = ess.getItemDb().serialize(is);
                list.add(serialized);
            }
        }

        ess.getSettings().addKit(kitname, list, delay);
        user.sendMessage(tl("createdKit", kitname, list.size(), delay));
    }
}
