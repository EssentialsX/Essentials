package com.neximation.essentials.commands;

import com.neximation.essentials.Trade;
import com.neximation.essentials.User;
import org.bukkit.Server;

import static com.neximation.essentials.I18n.tl;


public class Commandback extends EssentialsCommand {
    public Commandback() {
        super("back");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (user.getLastLocation() == null) {
            throw new Exception(tl("noLocationFound"));
        }
        if (user.getWorld() != user.getLastLocation().getWorld() && ess.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("essentials.worlds." + user.getLastLocation().getWorld().getName())) {
            throw new Exception(tl("noPerm", "essentials.worlds." + user.getLastLocation().getWorld().getName()));
        }
        final Trade charge = new Trade(this.getName(), ess);
        charge.isAffordableFor(user);
        user.getTeleport().back(charge);
        throw new NoChargeException();
    }
}
