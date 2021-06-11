package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DescParseTickFormat;
import net.ess3.api.IEssentials;

import static com.earth2me.essentials.I18n.tl;

public class SignTime extends EssentialsSign {
    public SignTime() {
        super("Time");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException {
        if (sign.getLine(1).isEmpty() && sign.getLine(2).isEmpty() && sign.getLine(3).isEmpty()) {
            return true;
        }

        validateTrade(sign, 2, ess);
        final String timeString = sign.getLine(1);
        if ("Day".equalsIgnoreCase(timeString)) {
            sign.setLine(1, "§2Day");
            return true;
        }
        if ("Night".equalsIgnoreCase(timeString)) {
            sign.setLine(1, "§2Night");
            return true;
        }
        throw new SignException(tl("onlyDayNight"));
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        if (sign.getLine(1).isEmpty() && sign.getLine(2).isEmpty() && sign.getLine(3).isEmpty()) {
            player.sendMessage(tl("timeWorldCurrentSign", DescParseTickFormat.format(player.getWorld().getTime())));
            return true;
        }

        final Trade charge = getTrade(sign, 2, ess);
        charge.isAffordableFor(player);
        final String timeString = sign.getLine(1);
        long time = player.getWorld().getTime();
        time -= time % 24000;
        if ("§2Day".equalsIgnoreCase(timeString)) {
            player.getWorld().setTime(time + 24000);
            charge.charge(player);
            Trade.log("Sign", "TimeDay", "Interact", username, null, username, charge, sign.getBlock().getLocation(), player.getMoney(), ess);
            return true;
        }
        if ("§2Night".equalsIgnoreCase(timeString)) {
            player.getWorld().setTime(time + 37700);
            charge.charge(player);
            Trade.log("Sign", "TimeNight", "Interact", username, null, username, charge, sign.getBlock().getLocation(), player.getMoney(), ess);
            return true;
        }
        throw new SignException(tl("onlyDayNight"));
    }
}
