package com.earth2me.essentials.user;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.storage.AsyncStorageObjectHolder;
import java.io.File;
import lombok.Delegate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.OfflinePlayer;


public abstract class UserBase extends AsyncStorageObjectHolder<UserData> implements Player, IOfflineUser
{
	
	@Delegate(types =
	{
		Player.class, Entity.class, CommandSender.class, ServerOperator.class,
		HumanEntity.class, ConfigurationSerializable.class, LivingEntity.class,
		Permissible.class
	},excludes=IOfflinePlayer.class)
	protected Player base;
	protected transient OfflinePlayer offlinePlayer;

	public UserBase(final Player base, final IEssentials ess)
	{
		super(ess, UserData.class);
		this.base = base;
		reloadConfig();
	}
	
	public UserBase(final OfflinePlayer offlinePlayer, final IEssentials ess)
	{
		super(ess, UserData.class);
		this.offlinePlayer = offlinePlayer;
		reloadConfig();
	}

	public final Player getBase()
	{
		return base;
	}

	public final Player setBase(final Player base)
	{
		return this.base = base;
	}

	public void update(final Player base)
	{
		setBase(base);
	}
	
	public void update(final OfflinePlayer offlinePlayer)
	{
		this.offlinePlayer = offlinePlayer;
	}
	
	public void dispose()
	{
		this.offlinePlayer = Bukkit.getOfflinePlayer(base.getName());
		this.base = null;
	}
	
	public boolean isOnlineUser() {
		return base != null;
	}

	@Override
	public String getName()
	{
		if (isOnlineUser()) {
			return base.getName();
		} else {
			return offlinePlayer.getName();
		}
	}
	
	@Override
	public String getDisplayName()
	{
		if (isOnlineUser()) {
			return base.getDisplayName();
		} else {
			return offlinePlayer.getName();
		}
	}
	
	@Override
	public Location getBedSpawnLocation()
	{
		return base.getBedSpawnLocation();
	}

	@Override
	public void setBanned(boolean bln)
	{
		if (isOnlineUser()) {
			base.setBanned(bln);
		} else {
			offlinePlayer.setBanned(bln);
		}
	}

	@Override
	public File getStorageFile()
	{
		return ess.getUserMap().getUserFile(getName());
	}
}
