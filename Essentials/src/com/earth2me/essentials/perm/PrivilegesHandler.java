package com.earth2me.essentials.perm;

import java.util.ArrayList;
import java.util.List;
import net.krinsoft.privileges.Privileges;
import net.krinsoft.privileges.groups.Group;
import net.krinsoft.privileges.groups.GroupManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PrivilegesHandler extends SuperpermsHandler
{
	private final transient Privileges plugin;
	private final GroupManager manager;

	public PrivilegesHandler(final Plugin plugin)
	{
		this.plugin = (Privileges) plugin;
		this.manager = this.plugin.getGroupManager();
	}

	@Override
	public String getGroup(final Player base)
	{
		Group group = manager.getGroup(base);
		if (group == null)
		{
			return null;
		}
		return group.getName();
	}

	@Override
	public List<String> getGroups(final Player base)
	{
		Group group = manager.getGroup(base);
		if (group == null)
		{
			return new ArrayList<String>();
		}
		return group.getGroupTree();
	}

	@Override
	public boolean inGroup(final Player base, final String group)
	{
		Group pGroup = manager.getGroup(base);
		if (pGroup == null)
		{
			return false;
		}
		return pGroup.isMemberOf(group);
	}

	@Override
	public boolean canBuild(Player base, String group)
	{
		return hasPermission(base, "essentials.build") || hasPermission(base, "privileges.build");
	}

}
