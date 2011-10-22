package com.earth2me.essentials;

import lombok.Delegate;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.ServerOperator;


public class PlayerExtension implements Player
{
	protected final transient IEssentials ess;
	@Delegate(types =
	{
		Player.class, Entity.class, CommandSender.class, ServerOperator.class, 
		HumanEntity.class, ConfigurationSerializable.class, LivingEntity.class,
		Permissible.class
	})
	protected Player base;
	
	public PlayerExtension(final Player base, final IEssentials ess)
	{
		this.base = base;
		this.ess = ess;
	}
	
	public final Player getBase()
	{
		return base;
	}

	public final Player setBase(final Player base)
	{
		return this.base = base;
	}
	
	public void showInventory(final IInventory inventory)
	{
		getHandle().a(inventory);
	}

	public void showInventory(final CraftInventoryPlayer inventory)
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
	
	public boolean inGroup(final String group)
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
