package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;


public class SignTrade extends EssentialsSign
{
	public SignTrade()
	{
		super("Trade");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		validateTrade(sign, 1, false, ess);
		validateTrade(sign, 2, true, ess);
		final Trade charge = getTrade(sign, 2, true, ess);
		charge.isAffordableFor(player);
		sign.setLine(3, "§8" + username);
		charge.charge(player);
		return true;
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		if (sign.getLine(3).substring(2).equalsIgnoreCase(username))
		{
			final Trade stored = getTrade(sign, 1, true, ess);
			substractAmount(sign, 1, stored);
			stored.pay(player);
		}
		else
		{
			final Trade charge = getTrade(sign, 1, false, ess);
			final Trade trade = getTrade(sign, 2, false, ess);
			charge.isAffordableFor(player);
			substractAmount(sign, 2, trade);
			trade.pay(player);
			addAmount(sign, 1, charge);
			charge.charge(player);
		}
		return true;
	}

	@Override
	protected boolean onSignBreak(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		if (sign.getLine(3).substring(2).equalsIgnoreCase(username))
		{
			final Trade stored1 = getTrade(sign, 1, true, ess);
			final Trade stored2 = getTrade(sign, 2, true, ess);
			stored1.pay(player);
			stored2.pay(player);
			return true;
		}
		else
		{
			return false;
		}
	}
}
