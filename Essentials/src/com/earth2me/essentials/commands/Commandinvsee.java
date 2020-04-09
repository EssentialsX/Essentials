package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.*;
import com.earth2me.essentials.User;
import org.bukkit.Location;
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

        if (ess.getSettings().isLimitInvseeDistance() && !user.isAuthorized("essentials.invsee.bypassdistance")) {
            checkDistance(user, invUser);
        }

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

    private void checkDistance(User invoker, User target) throws Exception {
        final Location invokerLoc = invoker.getLocation();
        final Location targetLoc = target.getLocation();

        if (invoker.getWorld() != target.getWorld()) {
            throw new Exception(tl("invseeDifferentWorld", target.getDisplayName()));
        }

        final long maxDistance = ess.getSettings().getInvseeRadius();
        final long maxSquared = maxDistance * maxDistance;
        final long delta = (long) invokerLoc.distanceSquared(targetLoc);

        if (delta > maxSquared) {
            throw new Exception(tl("invseeTooFar", maxDistance));
        }
    }
}
