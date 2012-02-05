package com.earth2me.essentials.signs;

import com.earth2me.essentials.api.IPermission;
import com.earth2me.essentials.perm.BasePermission;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class SignsPermissions
{
	public static final IPermission COLOR = new BasePermission("essentials.signs.", "color");
	public static final IPermission PROTECTION_OVERRIDE = new BasePermission("essentials.signs.protection.", "override");
	public static final IPermission TRADE_OVERRIDE = new BasePermission("essentials.signs.trade.", "override");
	private static Map<String, IPermission> createpermissions = new HashMap<String, IPermission>();

	public static IPermission getCreatePermission(final String signName)
	{
		IPermission perm = createpermissions.get(signName);
		if (perm == null)
		{
			perm = new BasePermission("essentials.signs.create.", signName.toLowerCase(Locale.ENGLISH));
			createpermissions.put(signName, perm);
		}
		return perm;
	}
	private static Map<String, IPermission> usepermissions = new HashMap<String, IPermission>();

	public static IPermission getUsePermission(final String signName)
	{
		IPermission perm = usepermissions.get(signName);
		if (perm == null)
		{
			perm = new BasePermission("essentials.signs.use.", signName.toLowerCase(Locale.ENGLISH));
			usepermissions.put(signName, perm);
		}
		return perm;
	}
	private static Map<String, IPermission> breakpermissions = new HashMap<String, IPermission>();

	public static IPermission getBreakPermission(final String signName)
	{
		IPermission perm = breakpermissions.get(signName);
		if (perm == null)
		{
			perm = new BasePermission("essentials.signs.break.", signName.toLowerCase(Locale.ENGLISH));
			breakpermissions.put(signName, perm);
		}
		return perm;
	}
}
