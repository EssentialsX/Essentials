package com.earth2me.essentials;

import lombok.Delegate;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.ServerOperator;


public class PlayerExtension
{
	@Delegate(types =
	{
		Player.class, Entity.class, CommandSender.class, ServerOperator.class,
		HumanEntity.class, ConfigurationSerializable.class, LivingEntity.class,
		Permissible.class
	})
	protected Player base;

	public PlayerExtension(final Player base)
	{
		this.base = base;
	}

	public final Player getBase()
	{
		return base;
	}

	public final Player setBase(final Player base)
	{
		return this.base = base;
	}
}
