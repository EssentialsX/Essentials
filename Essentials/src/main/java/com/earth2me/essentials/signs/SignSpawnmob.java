package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.SpawnMob;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;

import java.util.List;

public class SignSpawnmob extends EssentialsSign {
    public SignSpawnmob() {
        super("Spawnmob");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        validateInteger(sign, 1);
        validateTrade(sign, 3, ess);
        return true;
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        final Trade charge = getTrade(sign, 3, ess);
        charge.isAffordableFor(player);

        try {
            final List<String> mobParts = SpawnMob.mobParts(sign.getLine(2));
            final List<String> mobData = SpawnMob.mobData(sign.getLine(2));
            SpawnMob.spawnmob(ess, ess.getServer(), player.getSource(), player, mobParts, mobData, Integer.parseInt(sign.getLine(1)));
        } catch (final Exception ex) {
            throw new SignException(ex.getMessage(), ex);
        }

        charge.charge(player);
        Trade.log("Sign", "Spawnmob", "Interact", username, null, username, charge, sign.getBlock().getLocation(), player.getMoney(), ess);
        return true;
    }
}
