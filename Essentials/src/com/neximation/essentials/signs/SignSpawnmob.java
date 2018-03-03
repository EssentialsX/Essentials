package com.neximation.essentials.signs;

import com.neximation.essentials.ChargeException;
import com.neximation.essentials.SpawnMob;
import com.neximation.essentials.Trade;
import com.neximation.essentials.User;
import net.ess3.api.IEssentials;

import java.util.List;


public class SignSpawnmob extends EssentialsSign {
    public SignSpawnmob() {
        super("Spawnmob");
    }

    @Override
    protected boolean onSignCreate(ISign sign, User player, String username, IEssentials ess) throws SignException, ChargeException {
        validateInteger(sign, 1);
        validateTrade(sign, 3, ess);
        return true;
    }

    @Override
    protected boolean onSignInteract(ISign sign, User player, String username, IEssentials ess) throws SignException, ChargeException {
        final Trade charge = getTrade(sign, 3, ess);
        charge.isAffordableFor(player);

        try {
            List<String> mobParts = SpawnMob.mobParts(sign.getLine(2));
            List<String> mobData = SpawnMob.mobData(sign.getLine(2));
            SpawnMob.spawnmob(ess, ess.getServer(), player.getSource(), player, mobParts, mobData, Integer.parseInt(sign.getLine(1)));
        } catch (Exception ex) {
            throw new SignException(ex.getMessage(), ex);
        }

        charge.charge(player);
        Trade.log("Sign", "Spawnmob", "Interact", username, null, username, charge, sign.getBlock().getLocation(), ess);
        return true;
    }
}
