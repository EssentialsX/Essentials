package com.earth2me.essentials.signs;

import com.earth2me.essentials.api.ChargeException;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.perm.KitPermissions;
import com.earth2me.essentials.settings.Kit;
import java.util.Locale;


public class SignKit extends EssentialsSign
{
	public SignKit()
	{
		super("Kit");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final IUser player, final String username, final IEssentials ess) throws SignException
	{
		validateTrade(sign, 3, ess);
		
		final String kitName = sign.getLine(1).toLowerCase(Locale.ENGLISH);

		if (kitName.isEmpty())
		{
			sign.setLine(1, "§dKit name!");
			return false;
		}
		else
		{
			try
			{
				ess.getKits().getKit(kitName);				
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
		final String kitName = sign.getLine(1).toLowerCase(Locale.ENGLISH);
		final String group = sign.getLine(2);
		if ((!group.isEmpty() && ("§2Everyone".equals(group) || ess.getGroups().inGroup(player, group)))
			|| (group.isEmpty() && KitPermissions.getPermission(kitName).isAuthorized(player)))
		{
			final Trade charge = getTrade(sign, 3, ess);
			charge.isAffordableFor(player);
			try
			{
				final Kit kit = ess.getKits().getKit(kitName);				
				ess.getKits().sendKit(player, kit);
								
				charge.charge(player);
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
