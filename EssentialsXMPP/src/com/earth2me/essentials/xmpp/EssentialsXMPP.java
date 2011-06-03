package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Util;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsXMPP extends JavaPlugin implements IEssentialsXMPP
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private static EssentialsXMPP instance = null;
	private transient UserManager users;
	private transient XMPPManager xmpp;

	public static IEssentialsXMPP getInstance()
	{
		return instance;
	}
	
	@Override
	public void onEnable()
	{
		instance = this;

		final IEssentials ess = Essentials.getStatic();
		if (ess == null)
		{
			LOGGER.log(Level.SEVERE, "Failed to load Essentials before EssentialsXMPP");
		}

		final PluginManager pluginManager = getServer().getPluginManager();
		final EssentialsXMPPPlayerListener playerListener = new EssentialsXMPPPlayerListener(ess);
		pluginManager.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
		pluginManager.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Monitor, this);
		pluginManager.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);

		users = new UserManager(this.getDataFolder());
		xmpp = new XMPPManager(this);
		
		ess.addReloadListener(users);
		ess.addReloadListener(xmpp);
		
		if (!this.getDescription().getVersion().equals(Essentials.getStatic().getDescription().getVersion())) {
			LOGGER.log(Level.WARNING, Util.i18n("versionMismatchAll"));
		}
		LOGGER.info(Util.format("loadinfo", this.getDescription().getName(), this.getDescription().getVersion(), Essentials.AUTHORS));
	}

	@Override
	public void onDisable()
	{
		xmpp.disconnect();
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args)
	{
		return Essentials.getStatic().onCommandEssentials(sender, command, commandLabel, args, EssentialsXMPP.class.getClassLoader(), "com.earth2me.essentials.xmpp.Command");
	}
	
	@Override
	public void setAddress(final Player user, final String address)
	{
		final String username = user.getName().toLowerCase();
		instance.users.setAddress(username, address);
	}

	@Override
	public String getAddress(final String name)
	{
		return instance.users.getAddress(name);
	}
	
	@Override
	public String getUserByAddress(final String address)
	{
		return instance.users.getUserByAddress(address);
	}

	@Override
	public boolean toggleSpy(final Player user)
	{
		final String username = user.getName().toLowerCase();
		final boolean spy = !instance.users.isSpy(username);
		instance.users.setSpy(username, spy);
		return spy;
	}

	@Override
	public String getAddress(final Player user)
	{
		return instance.users.getAddress(user.getName());
	}

	@Override
	public void sendMessage(final Player user, final String message)
	{
		instance.xmpp.sendMessage(instance.users.getAddress(user.getName()), message);
	}

	@Override
	public void sendMessage(final String address, final String message)
	{
		instance.xmpp.sendMessage(address, message);
	}

	@Override
	public List<String> getSpyUsers()
	{
		return instance.users.getSpyUsers();
	}
}
