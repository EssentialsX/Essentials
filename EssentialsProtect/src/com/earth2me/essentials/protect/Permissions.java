package com.earth2me.essentials.protect;

import com.earth2me.essentials.api.IPermission;
import java.util.Locale;


public enum Permissions implements IPermission
{
	BUILD,
	PREVENTDAMAGE_FALL,
	PREVENTDAMAGE_NONE
	;
	private static final String base = "essentials.protect.";
	private final String permission;

	private Permissions()
	{
		permission = base + toString().toLowerCase(Locale.ENGLISH).replace('_', '.');
	}

	@Override
	public String getPermission()
	{
		return permission;
	}
}
