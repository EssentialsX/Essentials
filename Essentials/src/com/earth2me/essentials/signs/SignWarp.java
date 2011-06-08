package com.earth2me.essentials.signs;

import com.earth2me.essentials.Charge;
import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;


public class SignWarp extends EssentialsSign
{
	public SignWarp()
	{
		super("Warp");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		validateCharge(sign, 3);
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
			if ("Everyone".equalsIgnoreCase(group))
			{
				sign.setLine(2, "§2Everyone");
			}
			return true;
		}
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		final String warpName = sign.getLine(1);
		final String group = sign.getLine(2);
		if ((!group.isEmpty()
			 && ("§2Everyone".equals(group)
				 || player.inGroup(group)))
			|| (!ess.getSettings().getPerWarpPermission() || player.isAuthorized("essentials.warp." + warpName)))
		{
			final Charge charge = getCharge(sign, 3, ess);
			try
			{
				player.getTeleport().warp(warpName, charge);
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
