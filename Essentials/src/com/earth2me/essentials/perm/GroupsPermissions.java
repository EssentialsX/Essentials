package com.earth2me.essentials.perm;

import com.earth2me.essentials.api.IPermission;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class GroupsPermissions
{
	private static Map<String, IPermission> permissions = new HashMap<String, IPermission>();

	public static IPermission getPermission(final String groupName)
	{
		IPermission perm = permissions.get(groupName);
		if (perm == null)
		{
			perm = new BasePermission("essentials.groups.",groupName.toLowerCase(Locale.ENGLISH));
			permissions.put(groupName, perm);
		}
		return perm;
	}
}
