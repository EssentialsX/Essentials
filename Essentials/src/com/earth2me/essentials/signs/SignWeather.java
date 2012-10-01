package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;


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
		sign.setLine(1, "§c<sun|storm>");
		throw new SignException(_("onlySunStorm"));
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
		throw new SignException(_("onlySunStorm"));
	}
}
