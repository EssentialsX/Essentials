package com.earth2me.essentials.perm;

import com.earth2me.essentials.api.IPermission;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class EnchantPermissions
{
	private static Map<String, IPermission> permissions = new HashMap<String, IPermission>();

	public static IPermission getPermission(final String enchantName)
	{
		IPermission perm = permissions.get(enchantName);
		if (perm == null)
		{
			perm = new BasePermission("essentials.enchant.",enchantName.toLowerCase(Locale.ENGLISH));
			permissions.put(enchantName, perm);
		}
		return perm;
	}
}