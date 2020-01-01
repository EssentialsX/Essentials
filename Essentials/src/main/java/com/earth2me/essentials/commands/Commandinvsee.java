package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.inventory.Inventory;

import java.util.Collections;
import java.util.List;

public class Commandinvsee extends EssentialsCommand {
    public Commandinvsee() {
        super("invsee");
    }

    //This method has a hidden param, which if given will display the equip slots. #easteregg
    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final User invUser = getPlayer(server, user, args, 0);
        Inventory inv;

        if (args.length > 1 && user.isAuthorized("essentials.invsee.equip")) {
            inv = server.createInventory(invUser.getBase(), 9, "Equipped");
            inv.setContents(invUser.getBase().getInventory().getArmorContents());
        } else {
            inv = invUser.getBase().getInventory();
        }
        user.getBase().closeInventory();
        user.getBase().openInventory(inv);
        user.setInvSee(true);
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (args.length == 1) {
            return getPlayers(server, user);
        } else {
            //if (args.length == 2) {
            //    return Lists.newArrayList("equipped");
            //}
            return Collections.emptyList();
        }
    }
}
