package com.earth2me.essentials.perm;

import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IGroups;
import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.api.IUser;
import java.text.MessageFormat;
import lombok.Cleanup;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.plugin.Plugin;

public class GMGroups implements IGroups {
	private final transient IEssentials ess;
	private final transient GroupManager groupManager;

	public GMGroups(final IEssentials ess, final Plugin groupManager)
	{
		this.ess = ess;
		this.groupManager = (GroupManager)groupManager;
	}

	@Override
	public double getHealCooldown(IUser player)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player.getBase());
		if (handler == null)
		{
			return 0;
		}
		return handler.getPermissionDouble(player.getName(), "healcooldown");
	}

	@Override
	public double getTeleportCooldown(IUser player)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player.getBase());
		if (handler == null)
		{
			return 0;
		}
		return handler.getPermissionDouble(player.getName(), "teleportcooldown");
	}

	@Override
	public double getTeleportDelay(IUser player)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player.getBase());
		if (handler == null)
		{
			return 0;
		}
		return handler.getPermissionDouble(player.getName(), "teleportdelay");
	}

	@Override
	public String getPrefix(IUser player)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player.getBase());
		if (handler == null)
		{
			return null;
		}
		return handler.getUserPrefix(player.getName());
	}

	@Override
	public String getSuffix(IUser player)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player.getBase());
		if (handler == null)
		{
			return null;
		}
		return handler.getUserSuffix(player.getName());
	}

	@Override
	public int getHomeLimit(IUser player)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player.getBase());
		if (handler == null)
		{
			return 0;
		}
		return handler.getPermissionInteger(player.getName(), "homes");
	}

	@Override
	public MessageFormat getChatFormat(final IUser player)
	{
			String format = getRawChatFormat(player);
			format = Util.replaceColor(format);
			format = format.replace("{DISPLAYNAME}", "%1$s");
			format = format.replace("{GROUP}", "{0}");
			format = format.replace("{MESSAGE}", "%2$s");
			format = format.replace("{WORLDNAME}", "{1}");
			format = format.replace("{SHORTWORLDNAME}", "{2}");
			format = format.replaceAll("\\{(\\D*)\\}", "\\[$1\\]");
			MessageFormat mFormat = new MessageFormat(format);
			return mFormat;		
	}
	
	private String getRawChatFormat(final IUser player)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player.getBase());
		if (handler != null)
		{
			String chatformat = handler.getPermissionString(player.getName(), "chatformat");
			if (chatformat != null && !chatformat.isEmpty()) {
				return chatformat;
			}
		}
		
		@Cleanup
		ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		return settings.getData().getChat().getDefaultFormat();
	}

	@Override
	public String getMainGroup(IUser player)
	{
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player.getBase());
		if (handler == null)
		{
			return null;
		}
		return handler.getGroup(player.getName());
	}

	@Override
	public boolean inGroup(IUser player, String groupname)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player.getBase());
		if (handler == null)
		{
			return false;
		}
		return handler.inGroup(player.getName(), groupname);
	}
}
