package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.IUser;
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
	private transient IEssentials ess;
	
	public static IEssentialsXMPP getInstance()
	{
		return instance;
	}

	@Override
	public void onEnable()
	{
		instance = this;

		final PluginManager pluginManager = getServer().getPluginManager();
		ess = (IEssentials)pluginManager.getPlugin("Essentials");
		if (ess == null)
		{
			LOGGER.log(Level.SEVERE, "Failed to load Essentials before EssentialsXMPP");
		}
		
		final EssentialsXMPPPlayerListener playerListener = new EssentialsXMPPPlayerListener(ess);
		pluginManager.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
		pluginManager.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Monitor, this);
		pluginManager.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);

		users = new UserManager(this.getDataFolder());
		xmpp = new XMPPManager(this);

		ess.addReloadListener(users);
		ess.addReloadListener(xmpp);

		if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion()))
		{
			LOGGER.log(Level.WARNING, Util.i18n("versionMismatchAll"));
		}
		LOGGER.info(Util.format("loadinfo", this.getDescription().getName(), this.getDescription().getVersion(), "essentials team"));
	}

	@Override
	public void onDisable()
	{
		xmpp.disconnect();
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args)
	{
		return ess.onCommandEssentials(sender, command, commandLabel, args, EssentialsXMPP.class.getClassLoader(), "com.earth2me.essentials.xmpp.Command", "essentials.");
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
	public IUser getUserByAddress(final String address)
	{
		String username = instance.users.getUserByAddress(address);
		return username == null ? null : ess.getUser(username);
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
	public boolean sendMessage(final Player user, final String message)
	{
		return instance.xmpp.sendMessage(instance.users.getAddress(user.getName()), message);
	}

	@Override
	public boolean sendMessage(final String address, final String message)
	{
		return instance.xmpp.sendMessage(address, message);
	}

	@Override
	public List<String> getSpyUsers()
	{
		return instance.users.getSpyUsers();
	}

	@Override
	public void broadcastMessage(final IUser sender, final String message, final String xmppAddress)
	{
		ess.broadcastMessage(sender, message);
		try
		{
			for (String address : getSpyUsers())
			{
				if (!address.equalsIgnoreCase(xmppAddress))
				{
					sendMessage(address, message);
				}
			}
		}
		catch (Exception ex)
		{
			// Ignore exceptions
		}
	}
}
