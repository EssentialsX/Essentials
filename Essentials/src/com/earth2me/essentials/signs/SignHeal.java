package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;


public class SignHeal extends EssentialsSign
{
	public SignHeal()
	{
		super("Heal");
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
		if (player.getHealth() == 0)
		{
			throw new SignException(_("healDead"));
		}
		final Trade charge = getTrade(sign, 1, ess);
		charge.isAffordableFor(player);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.sendMessage(_("youAreHealed"));
		charge.charge(player);
		Trade.log("Sign", "Heal", "Interact", username, null, username, charge, sign.getBlock().getLocation(), ess);
		return true;
	}
}
