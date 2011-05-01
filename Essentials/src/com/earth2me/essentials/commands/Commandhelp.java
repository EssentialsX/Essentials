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
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


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
		int page;
		try
		{
			page = args.length > 0 ? Integer.parseInt(args[0]) : 1;
		}
		catch (Exception ex)
		{
			page = 1;
		}

		List<String> lines = getHelpLines(user);
		int start = (page - 1) * 9;
		int pages = lines.size() / 9 + (lines.size() % 9 > 0 ? 1 : 0);

		user.sendMessage("Page §c" + page + "§f of §c" + pages + "§f:");
		for (int i = start; i < lines.size() && i < start + 9; i++)
		{
			user.sendMessage(lines.get(i));
		}
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		sender.sendMessage("To view help from the console, type \"?\".");
	}

	@SuppressWarnings("CallToThreadDumpStack")
	private List<String> getHelpLines(User user) throws Exception
	{
		List<String> retval = new ArrayList<String>();
		File file = new File(ess.getDataFolder(), "help.txt");
		if (file.exists())
		{
			BufferedReader rx = new BufferedReader(new FileReader(file));
			for (String l = null; rx.ready() && (l = rx.readLine()) != null;)
			{
				retval.add(l.replace('&', '§'));
			}
			return retval;
		}

		boolean reported = false;
		for (Plugin p : ess.getServer().getPluginManager().getPlugins())
		{
			try
			{
				PluginDescriptionFile desc = p.getDescription();
				HashMap<String, HashMap<String, String>> cmds = (HashMap<String, HashMap<String, String>>)desc.getCommands();
				for (String k : cmds.keySet())
				{
					if (p.getDescription().getName().toLowerCase().contains("essentials"))
					{
						String node = "essentials." + k;
						if (!ess.getSettings().isCommandDisabled(k) && user.isAuthorized(node))
						{
							HashMap<String, String> v = cmds.get(k);
							retval.add("§c" + k + "§7: " + v.get("description"));
						}
					}
					else
					{
						if (ess.getSettings().showNonEssCommandsInHelp())
						{
							HashMap<String, String> v = cmds.get(k);
							if (v.containsKey("permission") && v.get("permission") != null && !(v.get("permission").equals("")))
							{
								if (user.isAuthorized(v.get("permission")))
								{
									retval.add("§c" + k + "§7: " + v.get("description"));
								}
							}
							else
							{
								retval.add("§c" + k + "§7: " + v.get("description"));
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
					ex.printStackTrace();
				}
				reported = true;
				continue;
			}
		}
		return retval;
	}
}
