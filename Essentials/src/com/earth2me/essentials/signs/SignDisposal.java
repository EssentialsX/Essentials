package com.earth2me.essentials.signs;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import net.minecraft.server.InventoryPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;


public class SignDisposal extends EssentialsSign
{
	public SignDisposal()
	{
		super("Disposal");
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess)
	{
		final CraftInventoryPlayer inv = new CraftInventoryPlayer(new InventoryPlayer(player.getHandle()));
		inv.clear();
		player.showInventory(inv);
		return true;
	}
}
