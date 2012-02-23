package com.earth2me.essentials.signs;

import com.earth2me.essentials.api.ChargeException;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.commands.Commandspawnmob;


public class SignSpawnmob extends EssentialsSign
{
	public SignSpawnmob()
	{
		super("Spawnmob");
	}

	@Override
	protected boolean onSignCreate(ISign sign, IUser player, String username, IEssentials ess) throws SignException, ChargeException
	{
		validateInteger(sign, 1);
		validateTrade(sign, 3, ess);
		return true;
	}

	
	//TODO: This should call a method not a command
	@Override
	protected boolean onSignInteract(ISign sign, IUser player, String username, IEssentials ess) throws SignException, ChargeException
	{
		final Trade charge = getTrade(sign, 3, ess);
		charge.isAffordableFor(player);
		Commandspawnmob command = new Commandspawnmob();	
		command.init(ess, "spawnmob");
		String[] args = new String[]
		{
			sign.getLine(2), sign.getLine(1)
		};
		try
		{
			command.run(player, "spawnmob", args);
		}
		catch (Exception ex)
		{
			throw new SignException(ex.getMessage(), ex);
		}
		charge.charge(player);
		return true;
	}
}
