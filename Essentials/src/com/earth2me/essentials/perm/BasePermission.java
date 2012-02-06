package com.earth2me.essentials.perm;

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
