package com.earth2me.essentials.signs;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class SignBalance extends EssentialsSign
{
	public SignBalance()
	{
		super("Balance");
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		player.sendMessage(Util.format("balance", player.getMoney()));
		return true;
	}
}
