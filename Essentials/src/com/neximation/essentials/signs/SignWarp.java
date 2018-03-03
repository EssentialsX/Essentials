package com.neximation.essentials.signs;

import com.neximation.essentials.ChargeException;
import com.neximation.essentials.Trade;
import com.neximation.essentials.User;
import net.ess3.api.IEssentials;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import static com.neximation.essentials.I18n.tl;


public class SignWarp extends EssentialsSign {
    public SignWarp() {
        super("Warp");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException {
        validateTrade(sign, 3, ess);
        final String warpName = sign.getLine(1);

        if (warpName.isEmpty()) {
            sign.setLine(1, "§c<Warp name>");
            throw new SignException(tl("invalidSignLine", 1));
        } else {
            try {
                ess.getWarps().getWarp(warpName);
            } catch (Exception ex) {
                throw new SignException(ex.getMessage(), ex);
            }
            final String group = sign.getLine(2);
            if ("Everyone".equalsIgnoreCase(group) || "Everybody".equalsIgnoreCase(group)) {
                sign.setLine(2, "§2Everyone");
            }
            return true;
        }
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        final String warpName = sign.getLine(1);
        final String group = sign.getLine(2);
        if ((!group.isEmpty() && ("§2Everyone".equals(group) || player.inGroup(group))) || (group.isEmpty() && (!ess.getSettings().getPerWarpPermission() || player.isAuthorized("essentials.warps." + warpName)))) {
            final Trade charge = getTrade(sign, 3, ess);
            try {
                player.getTeleport().warp(player, warpName, charge, TeleportCause.PLUGIN);
                Trade.log("Sign", "Warp", "Interact", username, null, username, charge, sign.getBlock().getLocation(), ess);
            } catch (Exception ex) {
                throw new SignException(ex.getMessage(), ex);
            }
            return true;
        }
        return false;
    }
}
