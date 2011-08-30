package com.earth2me.essentials.perm;

import java.util.List;
import org.bukkit.entity.Player;


public interface IPermissionsHandler
{
	String getGroup(Player base);

	List<String> getGroups(Player base);

	boolean canBuild(Player base, String group);

	boolean inGroup(Player base, String group);

	boolean hasPermission(Player base, String node);

	String getPrefix(Player base);

	String getSuffix(Player base);
}
