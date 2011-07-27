package com.earth2me.essentials.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.Map.Entry;
import java.util.logging.Level;


public class Commandhelp extends EssentialsCommand
{
	public final Yaml yaml = new Yaml(new SafeConstructor());

	public Commandhelp()
	{
		super("help");
	}

	@Override
	protected void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		int page = 1;
		String match = "";
		try
		{
			if (args.length > 0)
			{
				match = args[0].toLowerCase();
				page = Integer.parseInt(args[args.length - 1]);
				if (args.length == 1)
				{
					match = "";
				}
			}

		}
		catch (Exception ex)
		{
			if (args.length == 1)
			{
				match = args[0].toLowerCase();
			}
		}

		List<String> lines = getHelpLines(user, match);
		if (lines.size() > 0)
		{
			int start = (page - 1) * 9;
			int pages = lines.size() / 9 + (lines.size() % 9 > 0 ? 1 : 0);

			user.sendMessage(Util.format("helpPages", page, pages));
			for (int i = start; i < lines.size() && i < start + 9; i++)
			{
				user.sendMessage(lines.get(i));
			}
		}
		else
		{
			user.sendMessage(Util.i18n("noHelpFound"));
		}
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		sender.sendMessage(Util.i18n("helpConsole"));
	}

	@SuppressWarnings("CallToThreadDumpStack")
	private List<String> getHelpLines(User user, String match) throws Exception
	{
		List<String> retval = new ArrayList<String>();
		File helpFile = new File(ess.getDataFolder(), "help_" + Util.sanitizeFileName(user.getName()) + ".txt");
		if (!helpFile.exists())
		{
			helpFile = new File(ess.getDataFolder(), "help_" + Util.sanitizeFileName(user.getGroup()) + ".txt");
		}
		if (!helpFile.exists())
		{
			helpFile = new File(ess.getDataFolder(), "help.txt");
		}
		if (helpFile.exists())
		{
			final BufferedReader bufferedReader = new BufferedReader(new FileReader(helpFile));
			try
			{

				while (bufferedReader.ready())
				{
					final String line = bufferedReader.readLine();
					retval.add(line.replace('&', '§'));
				}
			}
			finally
			{
				bufferedReader.close();
			}
			return retval;
		}

		boolean reported = false;
		String pluginName = "";
		for (Plugin p : ess.getServer().getPluginManager().getPlugins())
		{
			try
			{
				final PluginDescriptionFile desc = p.getDescription();
				final HashMap<String, HashMap<String, String>> cmds = (HashMap<String, HashMap<String, String>>)desc.getCommands();
				for (Entry<String, HashMap<String, String>> k : cmds.entrySet())
				{
					if ((!match.equalsIgnoreCase("")) && (!k.getKey().toLowerCase().contains(match))
						&& (!k.getValue().get("description").toLowerCase().contains(match)))
					{
						continue;
					}

					if (p.getDescription().getName().toLowerCase().contains("essentials"))
					{
						final String node = "essentials." + k.getKey();
						if (!ess.getSettings().isCommandDisabled(k.getKey()) && user.isAuthorized(node))
						{
							retval.add("§c" + k.getKey() + "§7: " + k.getValue().get("description"));
						}
					}
					else
					{
						if (ess.getSettings().showNonEssCommandsInHelp())
						{
							pluginName = p.getDescription().getName();
							final HashMap<String, String> value = k.getValue();
							if (value.containsKey("permission") && value.get("permission") != null && !(value.get("permission").equals("")))
							{
								if (user.isAuthorized(value.get("permission")))
								{
									retval.add("§c" + k.getKey() + "§7: " + value.get("description"));
								}
							}
							else if (value.containsKey("permissions") && value.get("permissions") != null && !(value.get("permissions").equals("")))
							{
								if (user.isAuthorized(value.get("permissions")))
								{
									retval.add("§c" + k.getKey() + "§7: " + value.get("description"));
								}
							}
							else if (user.isAuthorized("essentials.help." + pluginName))
							{
								retval.add("§c" + k.getKey() + "§7: " + value.get("description"));								
							}		
							else
							{
								if (!ess.getSettings().hidePermissionlessHelp())
								{
									retval.add("§c" + k.getKey() + "§7: " + value.get("description"));
								}
							}
						}

					}
				}
			}
			catch (NullPointerException ex)
			{
				continue;
			}
			catch (Exception ex)
			{
				if (!reported)
				{
					logger.log(Level.WARNING, Util.format("commandHelpFailedForPlugin", pluginName), ex);
				}
				reported = true;
				continue;
			}
		}
		return retval;
	}
}
