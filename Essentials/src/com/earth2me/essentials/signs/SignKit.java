package com.earth2me.essentials.signs;

import com.earth2me.essentials.*;
import com.earth2me.essentials.commands.NoChargeException;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class SignKit extends EssentialsSign
{
	public SignKit()
	{
		super("Kit");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		validateTrade(sign, 3, ess);

		final String kitName = sign.getLine(1).toLowerCase(Locale.ENGLISH).trim();

		if (kitName.isEmpty())
		{
			sign.setLine(1, "§dKit name!");
			return false;
		}
		else
		{
			try
			{
				ess.getSettings().getKit(kitName);
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
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		final String kitName = sign.getLine(1).toLowerCase(Locale.ENGLISH).trim();
		final String group = sign.getLine(2);
		if ((!group.isEmpty() && ("§2Everyone".equals(group) || player.inGroup(group)))
			|| (group.isEmpty() && (player.isAuthorized("essentials.kit." + kitName))))
		{
			final Trade charge = getTrade(sign, 3, ess);
			charge.isAffordableFor(player);
			try
			{
				final Map<String, Object> kit = ess.getSettings().getKit(kitName);
				Kit.checkTime(player, kitName, kit);
				final List<String> items = Kit.getItems(player, kit);
				Kit.expandItems(ess, player, items);
				charge.charge(player);
			}
			catch (NoChargeException ex)
			{
				return false;
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
