package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tlp;


public class Commandback extends EssentialsCommand {
    public Commandback() {
        super("back");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (user.getLastLocation() == null) {
            throw new Exception(tlp(user, "noLocationFound"));
        }
        if (user.getWorld() != user.getLastLocation().getWorld() && ess.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("essentials.worlds." + user.getLastLocation().getWorld().getName())) {
            throw new Exception(tlp(user, "noPerm", "essentials.worlds." + user.getLastLocation().getWorld().getName()));
        }
        final Trade charge = new Trade(this.getName(), ess);
        charge.isAffordableFor(user);
        user.getTeleport().back(charge);
        throw new NoChargeException();
    }
}
