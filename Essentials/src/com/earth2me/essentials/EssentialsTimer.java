package com.earth2me.essentials;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;


public class EssentialsTimer implements Runnable, IConf
{
	private Essentials parent;
	private Set<User> allUsers = new HashSet<User>(); 
	
	EssentialsTimer(Essentials parent)
	{
		this.parent = parent;
		File userdir = new File(parent.getDataFolder(), "userdata");
		if (!userdir.exists()) {
			return;
		}
		for (String string : userdir.list())
		{
			if (!string.endsWith(".yml")) {
				continue;
			}
			String name = string.substring(0, string.length()-4);
			User u = parent.getUser(new OfflinePlayer(name));
			allUsers.add(u);
		}
	}

	public void run()
	{
		long currentTime = System.currentTimeMillis();
		for (Player player : parent.getServer().getOnlinePlayers())
		{
			User u = parent.getUser(player);
			allUsers.add(u);
			u.setLastActivity(currentTime);
		}
		
		for (User user: allUsers) {
			if (user.getBanTimeout() > 0 && user.getBanTimeout() < currentTime) {
				user.setBanTimeout(0);
				((CraftServer)parent.getServer()).getHandle().b(user.getName());
				Essentials.getStatic().loadBanList();
			}
			if (user.getMuteTimeout() > 0 && user.getMuteTimeout() < currentTime && user.isMuted()) {
				user.setMuteTimeout(0);
				user.sendMessage(Util.i18n("canTalkAgain"));
				user.setMuted(false);
			}
			if (user.getJailTimeout() > 0 && user.getJailTimeout() < currentTime && user.isJailed()) {
				user.setJailTimeout(0);
				user.setJailed(false);
				user.sendMessage(Util.i18n("haveBeenReleased"));
				user.setJail(null);
				try
				{
					user.getTeleport().back();
				}
				catch (Exception ex)
				{
				}
			}
			
			if (user.getLastActivity() < currentTime && user.getLastActivity() > user.getLastLogout()) {
				user.setLastLogout(user.getLastActivity());
			}
		}
	}

	public void reloadConfig()
	{
		for (User user : allUsers)
		{
			user.reloadConfig();
		}
	}
}
