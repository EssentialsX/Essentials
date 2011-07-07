package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class SignWeather extends EssentialsSign
{
	public SignWeather()
	{
		super("Weather");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		validateTrade(sign, 2, ess);
		final String timeString = sign.getLine(1);
		if ("Sun".equalsIgnoreCase(timeString))
		{
			sign.setLine(1, "§2Sun");
			return true;
		}
		if ("Storm".equalsIgnoreCase(timeString))
		{
			sign.setLine(1, "§2Storm");
			return true;
		}
		throw new SignException(Util.i18n("onlySunStorm"));
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		final Trade charge = getTrade(sign, 2, ess);
		charge.isAffordableFor(player);
		final String weatherString = sign.getLine(1);
		if ("§2Sun".equalsIgnoreCase(weatherString))
		{
			player.getWorld().setStorm(false);
			charge.charge(player);
			return true;
		}
		if ("§2Storm".equalsIgnoreCase(weatherString))
		{
			player.getWorld().setStorm(true);
			charge.charge(player);
			return true;
		}
		throw new SignException(Util.i18n("onlySunStorm"));
	}
}

