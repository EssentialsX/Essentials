package net.mcserverlist.bukkit;

import com.earth2me.essentials.Essentials;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;


public class Mcsl extends JavaPlugin
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	private McslPlayerListener playerListener;
	public final String author;

	public Mcsl() throws IOException
	{
		
		PluginDescriptionFile desc = this.getDescription();

		// Compile author list
		List<String> authors = new ArrayList<String>();
		authors.add("Vimae Development");
		int alen = authors.size();
		if (alen == 1)
		{
			author = " by " + authors.get(0);
		}
		else if (alen > 1)
		{
			int i = 0;
			StringBuilder bldr = new StringBuilder();
			for (String a : desc.getAuthors())
			{
				if (i + 1 == alen)
				{
					if (alen > 2) bldr.append(",");
					bldr.append(" and ");
				}
				else if (i++ > 0)
				{
					bldr.append(", ");
				}
				bldr.append(a);
			}
			bldr.insert(0, " by ");
			author = bldr.toString();
		}
		else
		{
			author = "";
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		McslCommands mcslCmd;
		try
		{
			switch (McslCommands.valueOf(cmd.getName().toUpperCase()))
			{
			case WHITELIST:
				whitelist(sender, WhitelistCommands.valueOf(args[0].toUpperCase()), args);
				return true;
				
			default:
				return false;
			}
		}
		catch (IllegalArgumentException ex)
		{
			return false;
		}
		catch (Exception ex)
		{
			logger.log(Level.WARNING, "MCSL encountered an unknown error.", ex);
			sender.sendMessage("MCSL encountered an unknown error.");
			return true;
		}
	}

	@SuppressWarnings("LoggerStringConcat")
	public void onEnable()
	{
		Plugin p = this.getServer().getPluginManager().getPlugin("Essentials");
            if (p != null) {
                if (!this.getServer().getPluginManager().isPluginEnabled(p)) {
                    this.getServer().getPluginManager().enablePlugin(p);
                }
		}
		playerListener = new McslPlayerListener(this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Lowest, this);

		if (!this.getDescription().getVersion().equals(Essentials.getStatic().getDescription().getVersion())) {
			logger.log(Level.WARNING, "Version mismatch! Please update all Essentials jars to the same version.");
		}
		logger.info(getDescription().getName() + " version " + getDescription().getVersion() + author + " enabled.");
	}

	@SuppressWarnings("LoggerStringConcat")
	public void onDisable()
	{
		logger.info(getDescription().getName() + " version " + getDescription().getVersion() + " disabled.");
	}

	private void whitelist(CommandSender sender, WhitelistCommands cmd, String[] args)
	{
		if (!playerListener.isWhitelistEnabled())
		{
			sender.sendMessage("§cThe whitelist is disabled.");
			return;
		}

		if (!sender.isOp())
		{
			sender.sendMessage("§cYou must be an operator to manage the whitelist.");
			return;
		}

		switch (cmd)
		{
		case RELOAD:
			playerListener.whitelistReload();
			sender.sendMessage("A whitelist updated has been queued.");
			break;
		}
	}

	private enum McslCommands
	{
		WHITELIST
	}

	private enum WhitelistCommands
	{
		RELOAD
	}
}
