package com.earth2me.essentials;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;


public class PlayerExtension extends PlayerWrapper
{
	protected final IEssentials ess;
	
	public PlayerExtension(Player base, IEssentials ess)
	{
		super(base);
		this.ess = ess;
	}

	public float getCorrectedYaw()
	{
		float angle = (getLocation().getYaw() - 90.0f) % 360.0f;
		if (angle < 0) {
			angle += 360.0f;
		}
		return angle;
	}
	
	public void showInventory(IInventory inventory)
	{
		getHandle().a(inventory);
	}

	public void showInventory(CraftInventoryPlayer inventory)
	{
		showInventory((IInventory)inventory.getInventory());
	}

	public TargetBlock getTarget()
	{
		return new TargetBlock(getBase());
	}

	public String getGroup()
	{
		return ess.getPermissionsHandler().getGroup(base);
	}
	
	public boolean inGroup(String group)
	{
		return ess.getPermissionsHandler().inGroup(base, group);
	}

	public boolean canBuild()
	{
		return ess.getPermissionsHandler().canBuild(base, getGroup());
	}

	public EntityPlayer getHandle()
	{
		return getCraftPlayer().getHandle();
	}

	public CraftPlayer getCraftPlayer()
	{
		return (CraftPlayer)base;
	}
}
