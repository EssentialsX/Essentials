package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;

public class SignWeather extends EssentialsSign {
    public SignWeather() {
        super("Weather");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException {
        if (sign.getLine(1).isEmpty() && sign.getLine(2).isEmpty() && sign.getLine(3).isEmpty()) {
            return true;
        }

        validateTrade(sign, 2, ess);
        final String timeString = sign.getLine(1);
        if ("Sun".equalsIgnoreCase(timeString)) {
            sign.setLine(1, "§2Sun");
            return true;
        }
        if ("Storm".equalsIgnoreCase(timeString)) {
            sign.setLine(1, "§2Storm");
            return true;
        }
        sign.setLine(1, "§c<sun|storm>");
        throw new SignException("onlySunStorm");
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        if (sign.getLine(1).isEmpty() && sign.getLine(2).isEmpty() && sign.getLine(3).isEmpty()) {
            if (player.getWorld().hasStorm()) {
                player.sendTl("weatherSignStorm");
            } else {
                player.sendTl("weatherSignSun");
            }
            return true;
        }

        final Trade charge = getTrade(sign, 2, ess);
        charge.isAffordableFor(player);
        final String weatherString = sign.getLine(1);
        if ("§2Sun".equalsIgnoreCase(weatherString)) {
            player.getWorld().setStorm(false);
            charge.charge(player);
            Trade.log("Sign", "WeatherSun", "Interact", username, null, username, charge, sign.getBlock().getLocation(), player.getMoney(), ess);
            return true;
        }
        if ("§2Storm".equalsIgnoreCase(weatherString)) {
            player.getWorld().setStorm(true);
            charge.charge(player);
            Trade.log("Sign", "WeatherStorm", "Interact", username, null, username, charge, sign.getBlock().getLocation(), player.getMoney(), ess);
            return true;
        }
        throw new SignException("onlySunStorm");
    }
}
