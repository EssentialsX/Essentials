package com.earth2me.essentials.perm;

import com.earth2me.essentials.api.IPermission;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class NoCommandCostPermissions
{
	private static Map<String, IPermission> permissions = new HashMap<String, IPermission>();

	public static IPermission getPermission(final String command)
	{
		IPermission perm = permissions.get(command);
		if (perm == null)
		{
			perm = new BasePermission("essentials.nocommandcost.", command.toLowerCase(Locale.ENGLISH));
			permissions.put(command, perm);
		}
		return perm;
	}
}
