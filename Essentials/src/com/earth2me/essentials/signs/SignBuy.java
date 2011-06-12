package com.earth2me.essentials.signs;

import com.earth2me.essentials.Charge;
import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.InventoryWorkaround;
import com.earth2me.essentials.User;
import java.util.Map;
import org.bukkit.inventory.ItemStack;


public class SignBuy extends EssentialsSign
{
	public SignBuy()
	{
		super("Buy");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		validateInteger(sign, 1);
		validateItem(sign, 2, true);
		validateCharge(sign, 3);
		return true;
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		final ItemStack item = getItemStack(sign.getLine(2));
		final int amount = Math.min(getInteger(sign.getLine(1)), item.getType().getMaxStackSize()*player.getInventory().getSize());
		item.setAmount(amount);
		final Charge charge = getCharge(sign, 3, ess);
		charge.isAffordableFor(player);
		final Map<Integer, ItemStack> leftOver = InventoryWorkaround.addItem(player.getInventory(), true, item);
		for (ItemStack itemStack : leftOver.values())
		{
			InventoryWorkaround.dropItem(player.getLocation(), itemStack);
		}
		player.updateInventory();
		charge.charge(player);
		return true;
	}
}
