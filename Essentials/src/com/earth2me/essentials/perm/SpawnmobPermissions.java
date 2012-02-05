package com.earth2me.essentials.perm;

import com.earth2me.essentials.api.IPermission;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SpawnmobPermissions {
	private static Map<String, IPermission> permissions = new HashMap<String, IPermission>();

	public static IPermission getPermission(final String mobName)
	{
		IPermission perm = permissions.get(mobName);
		if (perm == null)
		{
			perm = new BasePermission("essentials.spawnmob.", mobName.toLowerCase(Locale.ENGLISH).replace("_", ""));
			permissions.put(mobName, perm);
		}
		return perm;
	}
}
