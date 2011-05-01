package com.earth2me.essentials.commands;

import java.io.File;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;


public class Commandplugin extends EssentialsCommand
{
	private Server server;

	public Commandplugin()
	{
		super("plugin");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		this.server = server;

		PluginCommands sub = null;
		try
		{
			sub = PluginCommands.valueOf(args[0].toUpperCase());
		}
		catch (Exception ex)
		{
			sender.sendMessage("§cUsage: /plugin [load|reload|enable|disable|list] [PluginName]");
			return;
		}

		switch (sub)
		{
		case LOAD: // All disable functions are broken until 
			// http://leaky.bukkit.org/issues/641 is fixed.
			sender.sendMessage("This function is broken. Performing /reload now.");
			server.reload();
			/*if (args.length < 2) return;
			User.charge(sender, this);
			loadPlugin(args[1], sender);*/
			return;

		case RELOAD:
			sender.sendMessage("This function is broken. Performing /reload now.");
			server.reload();
			/*if (args.length < 2) return;
			User.charge(sender, this);
			reloadPlugin(args[1], sender);*/
			return;

		case ENABLE:
			sender.sendMessage("This function is broken. Performing /reload now.");
			server.reload();
			/*if (args.length < 2) return;
			User.charge(sender, this);
			enablePlugin(args[1], sender);*/
			return;

		case DISABLE:
			sender.sendMessage("This function is broken.");
			/*if (args.length < 2) return;
			User.charge(sender, this);
			disablePlugin(args[1], sender);*/
			return;

		case LIST:
			charge(sender);
			listPlugins(sender);
			return;
		}
	}

	private void listPlugins(CommandSender player)
	{
		StringBuilder plugins = new StringBuilder();
		for (Plugin p : server.getPluginManager().getPlugins())
		{
			plugins.append(p.isEnabled() ? " §a" : " §c");
			plugins.append(p.getDescription().getName());
		}

		plugins.insert(0, "§7Plugins:§f");
		player.sendMessage(plugins.toString());
	}

	private boolean reloadPlugin(String name, CommandSender player)
	{
		return disablePlugin(name, player) && enablePlugin(name, player);
	}

	private boolean loadPlugin(String name, CommandSender sender)
	{
		try
		{
			PluginManager pm = server.getPluginManager();
			pm.loadPlugin(new File("plugins", name + ".jar"));
			sender.sendMessage("§7Plugin loaded.");
			return enablePlugin(name, sender);
		}
		catch (Throwable ex)
		{
			sender.sendMessage("§cCould not load plugin. Is the file named properly?");
			return false;
		}
	}

	private boolean enablePlugin(String name, CommandSender sender)
	{
		try
		{
			final PluginManager pm = server.getPluginManager();
			final Plugin plugin = pm.getPlugin(name);
			if (!plugin.isEnabled()) 
			{	
				new Thread(new Runnable()
				{
					public void run()
					{
						synchronized (pm)
						{
							pm.enablePlugin(plugin);
						}
					}
				}).start();
			}
			sender.sendMessage("§7Plugin enabled.");
			return true;
		}
		catch (Throwable ex)
		{
			listPlugins(sender);
			return false;
		}
	}

	private boolean disablePlugin(String name, CommandSender sender)
	{
		try
		{
			final PluginManager pm = server.getPluginManager();
			final Plugin plugin = pm.getPlugin(name);
			if (plugin.isEnabled())
			{
				new Thread(new Runnable()
				{
					public void run()
					{
						synchronized (pm)
						{
							pm.disablePlugin(plugin);
						}
					}
				}).start();
			}
			sender.sendMessage("§7Plugin disabled.");
			return true;
		}
		catch (Throwable ex)
		{
			listPlugins(sender);
			return false;
		}
	}


	private enum PluginCommands
	{
		LOAD, RELOAD, LIST, ENABLE, DISABLE
	}
}
