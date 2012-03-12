package com.earth2me.essentials.textreader;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;


public class HelpInput implements IText
{
	private static final String DESCRIPTION = "description";
	private static final String PERMISSION = "permission";
	private static final String PERMISSIONS = "permissions";
	private final transient List<String> lines = new ArrayList<String>();
	private final transient List<String> chapters = new ArrayList<String>();
	private final transient Map<String, Integer> bookmarks = new HashMap<String, Integer>();
	private final static Logger logger = Logger.getLogger("Minecraft");

	public HelpInput(final User user, final String match, final IEssentials ess) throws IOException
	{
		boolean reported = false;
		String pluginName = "";
		for (Plugin p : ess.getServer().getPluginManager().getPlugins())
		{
			try
			{
				final PluginDescriptionFile desc = p.getDescription();
				final Map<String, Map<String, Object>> cmds = desc.getCommands();
				pluginName = p.getDescription().getName().toLowerCase(Locale.ENGLISH);
				for (Map.Entry<String, Map<String, Object>> k : cmds.entrySet())
				{
					try
					{
						if ((!match.equalsIgnoreCase(""))
							&& (!k.getKey().toLowerCase(Locale.ENGLISH).contains(match))
							&& (!(k.getValue().get(DESCRIPTION) instanceof String
								  && ((String)k.getValue().get(DESCRIPTION)).toLowerCase(Locale.ENGLISH).contains(match)))
							&& (!pluginName.contains(match)))
						{
							continue;
						}

						if (pluginName.contains("essentials"))
						{
							final String node = "essentials." + k.getKey();
							if (!ess.getSettings().isCommandDisabled(k.getKey()) && user.isAuthorized(node))
							{
								lines.add(_("helpLine", k.getKey(), k.getValue().get(DESCRIPTION)));
							}
						}
						else
						{
							if (ess.getSettings().showNonEssCommandsInHelp())
							{
								final Map<String, Object> value = k.getValue();
								Object permissions = null;
								if (value.containsKey(PERMISSION))
								{
									permissions = value.get(PERMISSION);
								}
								else if (value.containsKey(PERMISSIONS))
								{
									permissions = value.get(PERMISSIONS);
								}
								if (user.isAuthorized("essentials.help." + pluginName))
								{
									lines.add(_("helpLine", k.getKey(), value.get(DESCRIPTION)));
								}
								else if (permissions instanceof List && !((List<Object>)permissions).isEmpty())
								{
									boolean enabled = false;
									for (Object o : (List<Object>)permissions)
									{
										if (o instanceof String && user.isAuthorized(o.toString()))
										{
											enabled = true;
											break;
										}
									}
									if (enabled)
									{
										lines.add(_("helpLine", k.getKey(), value.get(DESCRIPTION)));
									}
								}
								else if (permissions instanceof String && !"".equals(permissions))
								{
									if (user.isAuthorized(permissions.toString()))
									{
										lines.add(_("helpLine", k.getKey(), value.get(DESCRIPTION)));
									}
								}
								else
								{
									if (!ess.getSettings().hidePermissionlessHelp())
									{
										lines.add(_("helpLine", k.getKey(), value.get(DESCRIPTION)));
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
					logger.log(Level.WARNING, _("commandHelpFailedForPlugin", pluginName), ex);
				}
				reported = true;
				continue;
			}
		}
	}

	@Override
	public List<String> getLines()
	{
		return lines;
	}

	@Override
	public List<String> getChapters()
	{
		return chapters;
	}

	@Override
	public Map<String, Integer> getBookmarks()
	{
		return bookmarks;
	}
}
