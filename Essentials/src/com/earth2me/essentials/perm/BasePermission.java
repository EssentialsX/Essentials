package com.earth2me.essentials.perm;

import com.earth2me.essentials.api.IPermission;

public class BasePermission extends AbstractSuperpermsPermission {
	protected String permission;

	public BasePermission(String base, String permission)
	{
		this.permission = base + permission;
		
	}

	public String getPermission()
	{
		return permission;
	}	
}
