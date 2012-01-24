package com.earth2me.essentials.api;

import com.earth2me.essentials.settings.Groups;
import com.earth2me.essentials.storage.IStorageObjectHolder;
import java.text.MessageFormat;


public interface IGroups extends IStorageObjectHolder<Groups>
{
	double getHealCooldown(IUser player);

	double getTeleportCooldown(IUser player);

	double getTeleportDelay(IUser player);

	String getPrefix(IUser player);

	String getSuffix(IUser player);

	int getHomeLimit(IUser player);

	MessageFormat getChatFormat(IUser player);
}
