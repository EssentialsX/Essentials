package com.earth2me.essentials;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.entity.*;


public class PlayerExtension extends PlayerWrapper
{
	public PlayerExtension(Player base)
	{
		super(base);
	}

	public boolean isBanned()
	{
		return Essentials.getStatic().bans.contains(getName());
	}

	public boolean isIpBanned()
	{
		return Essentials.getStatic().bannedIps.contains(getAddress().getAddress().toString().replace("/", ""));
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
		try
		{
			return com.nijikokun.bukkit.Permissions.Permissions.Security.getGroup(getWorld().getName(), getName());
		}
		catch (Throwable ex)
		{
			return "default";
		}
	}
	
	public boolean inGroup(String group)
	{
		try
		{
			return com.nijikokun.bukkit.Permissions.Permissions.Security.inGroup(getWorld().getName(), getName(), group);
		}
		catch (Throwable ex)
		{
			return false;
		}
	}

	public boolean canBuild()
	{
		try
		{
			return com.nijikokun.bukkit.Permissions.Permissions.Security.canGroupBuild(getWorld().getName(), getGroup());
		}
		catch (Throwable ex)
		{
			return true;
		}
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
