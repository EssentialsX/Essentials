package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;


public class Commandback extends EssentialsCommand {
    public Commandback() {
        super("back");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (user.getLastLocation() == null) {
            throw new Exception(tl("noLocationFound"));
        }

        String lastWorldName = user.getLastLocation().getWorld().getName();

        if (user.getWorld() != user.getLastLocation().getWorld() && ess.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("essentials.worlds." + lastWorldName)) {
            throw new Exception(tl("noPerm", "essentials.worlds." + lastWorldName));
        }

        if (!user.isAuthorized("essentials.back.into." + lastWorldName)) {
            throw new Exception(tl("noPerm", "essentials.back.into." + lastWorldName));
        }

        final Trade charge = new Trade(this.getName(), ess);
        charge.isAffordableFor(user);
        user.getTeleport().back(charge);
        throw new NoChargeException();
    }
}
