package com.earth2me.essentials.api;

import java.text.MessageFormat;


public interface IGroups
{
	String getMainGroup(IUser player);

	boolean inGroup(IUser player, String groupname);

	double getHealCooldown(IUser player);

	double getTeleportCooldown(IUser player);

	double getTeleportDelay(IUser player);

	String getPrefix(IUser player);

	String getSuffix(IUser player);

	int getHomeLimit(IUser player);

	MessageFormat getChatFormat(IUser player);
}
