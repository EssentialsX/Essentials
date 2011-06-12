package com.earth2me.essentials.signs;

import com.earth2me.essentials.Charge;
import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import org.bukkit.inventory.ItemStack;


public class SignSell extends EssentialsSign
{
	public SignSell()
	{
		super("Sell");
	}
	
	@Override
	protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		validateInteger(sign, 1);
		validateItem(sign, 2, true);
		validateMoney(sign, 3);
		return true;
	}
	
	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		final int amount = getInteger(sign.getLine(1));
		final ItemStack item = getItemStack(sign.getLine(2));
		item.setAmount(amount);
		final double money = getMoney(sign.getLine(3));
		final Charge charge = new Charge(item, ess);
		charge.isAffordableFor(player);
		player.giveMoney(money);
		charge.charge(player);
		return true;
	}
}
