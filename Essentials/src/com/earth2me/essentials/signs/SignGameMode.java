package com.earth2me.essentials.signs;

import com.earth2me.essentials.*;
import org.bukkit.GameMode;


public class SignGameMode extends EssentialsSign
{
	public SignGameMode()
	{
		super("GameMode");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		validateTrade(sign, 1, ess);
		return true;
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		final Trade charge = getTrade(sign, 1, ess);
		charge.isAffordableFor(player);

		player.setGameMode(player.getGameMode() == GameMode.SURVIVAL ? GameMode.CREATIVE : GameMode.SURVIVAL);
		player.sendMessage(Util.format("gameMode", Util.i18n(player.getGameMode().toString().toLowerCase()), player.getDisplayName()));
		charge.charge(player);
		return true;
	}
}
