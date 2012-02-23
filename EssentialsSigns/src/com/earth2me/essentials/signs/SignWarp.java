package com.earth2me.essentials.signs;

import com.earth2me.essentials.api.ChargeException;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.perm.WarpPermissions;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class SignWarp extends EssentialsSign
{
	public SignWarp()
	{
		super("Warp");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final IUser player, final String username, final IEssentials ess) throws SignException
	{
		validateTrade(sign, 3, ess);
		final String warpName = sign.getLine(1);

		if (warpName.isEmpty())
		{
			sign.setLine(1, "§dWarp name!");
			return false;
		}
		else
		{
			try
			{
				ess.getWarps().getWarp(warpName);
			}
			catch (Exception ex)
			{
				throw new SignException(ex.getMessage(), ex);
			}
			final String group = sign.getLine(2);
			if ("Everyone".equalsIgnoreCase(group) || "Everybody".equalsIgnoreCase(group))
			{
				sign.setLine(2, "§2Everyone");
			}
			return true;
		}
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final IUser player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		final String warpName = sign.getLine(1);
		final String group = sign.getLine(2);

		if ((!group.isEmpty() && ("§2Everyone".equals(group) || ess.getGroups().inGroup(player, group)))
			|| (group.isEmpty() && WarpPermissions.getPermission(warpName).isAuthorized(player)))
		{
			final Trade charge = getTrade(sign, 3, ess);
			try
			{
				player.getTeleport().warp(warpName, charge, TeleportCause.PLUGIN);
			}
			catch (Exception ex)
			{
				throw new SignException(ex.getMessage(), ex);
			}
			return true;
		}
		return false;
	}
}
