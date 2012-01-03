package com.earth2me.essentials.perm;

import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IPermission;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;


public abstract class AbstractSuperpermsPermission implements IPermission
{
	protected Permission bukkitPerm;

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
		return PermissionDefault.OP;
	}
}
