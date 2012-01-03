package com.earth2me.essentials.perm;

import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IPermission;
import java.util.Locale;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;


public enum Permissions implements IPermission
{
	AFK_OTHERS,
	BALANCE_OTHERS;
	private static final String base = "essentials.";
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
