package com.earth2me.essentials.signs;

import com.earth2me.essentials.Charge;
import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class SignHeal extends EssentialsSign
{
	public SignHeal()
	{
		super("Heal");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		validateCharge(sign, 1);
		return true;
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		final Charge charge = getCharge(sign, 1, ess);
		charge.isAffordableFor(player);
		player.setHealth(20);
		player.sendMessage(Util.i18n("youAreHealed"));
		charge.charge(player);
		return true;
	}
}
