package com.earth2me.essentials.perm;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.bpermissions.api.util.Calculable;
import de.bananaco.bpermissions.api.util.CalculableType;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;


public class BPermissions2Handler extends SuperpermsHandler
{
	public BPermissions2Handler()
	{
		
	}

	@Override
	public String getGroup(final Player base)
	{
		final List<String> groups = getGroups(base);
		if (groups == null || groups.isEmpty())
		{
			return null;
		}
		return groups.get(0);
	}

	@Override
	public List<String> getGroups(final Player base)
	{
		final String[] groups = ApiLayer.getGroups(base.getWorld().getName(), CalculableType.USER, base.getName());
		return Arrays.asList(groups);		
	}

	@Override
	public boolean inGroup(final Player base, final String group)
	{
		return ApiLayer.hasGroup(base.getWorld().getName(), CalculableType.USER, base.getName(), group);
	}

	@Override
	public boolean canBuild(final Player base, final String group)
	{
		return hasPermission(base, "bPermissions.build");
	}

	@Override
	public String getPrefix(final Player base)
	{
		return ApiLayer.getValue(base.getWorld().getName(), CalculableType.USER, base.getName(), "prefix");
	}

	@Override
	public String getSuffix(final Player base)
	{
		return ApiLayer.getValue(base.getWorld().getName(), CalculableType.USER, base.getName(), "suffix");		
	}

}
