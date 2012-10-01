package com.earth2me.essentials.signs;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class SignFree extends EssentialsSign
{
	public SignFree()
	{
		super("Free");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		try {
			getItemStack(sign.getLine(1), 1, ess);
		}
		catch (SignException ex)
		{
			sign.setLine(1, "§c<item>");
			throw new SignException(ex.getMessage(), ex);
		}
		return true;
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		final ItemStack item = getItemStack(sign.getLine(1), 1, ess);
		if (item.getType() == Material.AIR)
		{
			throw new SignException(_("cantSpawnItem", "Air"));
		}

		item.setAmount(item.getType().getMaxStackSize());
		Inventory invent = ess.getServer().createInventory(player, 36);
		for (int i = 0; i < 36; i++) {
			invent.addItem(item);
		}
		player.openInventory(invent);
		Trade.log("Sign", "Free", "Interact", username, null, username, new Trade(item, ess), sign.getBlock().getLocation(), ess);
		return true;
	}
}
