package com.earth2me.essentials.protect;

import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IPermission;
import java.util.Locale;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;


public enum Permissions implements IPermission
{
	BUILD(PermissionDefault.TRUE),
	INTERACT(PermissionDefault.TRUE),
	USEFLINTSTEEL(PermissionDefault.TRUE),
	PREVENTDAMAGE_FALL(PermissionDefault.FALSE),
	PREVENTDAMAGE_SUFFOCATION(PermissionDefault.FALSE),
	PREVENTDAMAGE_FIRE(PermissionDefault.FALSE),
	PREVENTDAMAGE_DROWNING(PermissionDefault.FALSE),
	PREVENTDAMAGE_LIGHTNING(PermissionDefault.FALSE),
	PREVENTDAMAGE_NONE(PermissionDefault.FALSE)
	;
	private static final String base = "essentials.protect.";
	private final String permission;
	private final PermissionDefault defaultPerm;
	private transient Permission bukkitPerm = null;

	private Permissions()
	{
		this(PermissionDefault.OP);
	}

	private Permissions(final PermissionDefault defaultPerm)
	{
		permission = base + toString().toLowerCase(Locale.ENGLISH).replace('_', '.');
		this.defaultPerm = defaultPerm;
	}

	@Override
	public String getPermission()
	{
		return permission;
	}

	@Override
	public Permission getBukkitPermission()
	{
		if (bukkitPerm != null)
		{
			return bukkitPerm;
		}
		else
		{
			return Util.registerPermission(getPermission(), getPermissionDefault());
		}
	}

	@Override
	public PermissionDefault getPermissionDefault()
	{
		return this.defaultPerm;
	}
}
