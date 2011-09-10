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
	private static final String DESCRIPTION = "description";
	private static final String PERMISSION = "permission";
	private static final String PERMISSIONS = "permissions";
	public final Yaml yaml = new Yaml(new SafeConstructor());

	public Commandhelp()
	{
		super("help");
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
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

		final List<String> lines = getHelpLines(user, match);
		if (lines.isEmpty())
		{
			throw new Exception(Util.i18n("noHelpFound"));
		}

		final int start = (page - 1) * 9;
		final int pages = lines.size() / 9 + (lines.size() % 9 > 0 ? 1 : 0);

		user.sendMessage(Util.format("helpPages", page, pages));
		for (int i = start; i < lines.size() && i < start + 9; i++)
		{
			user.sendMessage(lines.get(i));
		}
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		sender.sendMessage(Util.i18n("helpConsole"));
	}

	@SuppressWarnings("CallToThreadDumpStack")
	private List<String> getHelpLines(final User user, final String match) throws Exception
	{
		final List<String> retval = new ArrayList<String>();
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
				final HashMap<String, HashMap<String, Object>> cmds = (HashMap<String, HashMap<String, Object>>)desc.getCommands();
				pluginName = p.getDescription().getName().toLowerCase();
				for (Entry<String, HashMap<String, Object>> k : cmds.entrySet())
				{
					try
					{
						if ((!match.equalsIgnoreCase(""))
							&& (!k.getKey().toLowerCase().contains(match))
							&& (!(k.getValue().get(DESCRIPTION) instanceof String
								  && ((String)k.getValue().get(DESCRIPTION)).toLowerCase().contains(match)))
							&& (!pluginName.contains(match)))
						{
							continue;
						}

						if (pluginName.contains("essentials"))
						{
							final String node = "essentials." + k.getKey();
							if (!ess.getSettings().isCommandDisabled(k.getKey()) && user.isAuthorized(node))
							{
								retval.add("§c" + k.getKey() + "§7: " + k.getValue().get(DESCRIPTION));
							}
						}
						else
						{
							if (ess.getSettings().showNonEssCommandsInHelp())
							{
								final HashMap<String, Object> value = k.getValue();
								if (value.containsKey(PERMISSION) && value.get(PERMISSION) instanceof String && !(value.get(PERMISSION).equals("")))
								{
									if (user.isAuthorized((String)value.get(PERMISSION)))
									{
										retval.add("§c" + k.getKey() + "§7: " + value.get(DESCRIPTION));
									}
								}
								else if (value.containsKey(PERMISSION) && value.get(PERMISSION) instanceof List && !((List<Object>)value.get(PERMISSION)).isEmpty())
								{
									boolean enabled = false;
									for (Object o : (List<Object>)value.get(PERMISSION))
									{
										if (o instanceof String && user.isAuthorized((String)o))
										{
											enabled = true;
											break;
										}
									}
									if (enabled)
									{
										retval.add("§c" + k.getKey() + "§7: " + value.get(DESCRIPTION));
									}
								}
								else if (value.containsKey(PERMISSIONS) && value.get(PERMISSIONS) instanceof String && !(value.get(PERMISSIONS).equals("")))
								{
									if (user.isAuthorized((String)value.get(PERMISSIONS)))
									{
										retval.add("§c" + k.getKey() + "§7: " + value.get(DESCRIPTION));
									}
								}
								else if (value.containsKey(PERMISSIONS) && value.get(PERMISSIONS) instanceof List && !((List<Object>)value.get(PERMISSIONS)).isEmpty())
								{
									boolean enabled = false;
									for (Object o : (List<Object>)value.get(PERMISSIONS))
									{
										if (o instanceof String && user.isAuthorized((String)o))
										{
											enabled = true;
											break;
										}
									}
									if (enabled)
									{
										retval.add("§c" + k.getKey() + "§7: " + value.get(DESCRIPTION));
									}
								}
								else if (user.isAuthorized("essentials.help." + pluginName))
								{
									retval.add("§c" + k.getKey() + "§7: " + value.get(DESCRIPTION));
								}
								else
								{
									if (!ess.getSettings().hidePermissionlessHelp())
									{
										retval.add("§c" + k.getKey() + "§7: " + value.get(DESCRIPTION));
									}
								}
							}
						}
					}
					catch (NullPointerException ex)
					{
						continue;
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
