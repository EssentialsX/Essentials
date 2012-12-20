package com.earth2me.essentials.textreader;

import com.earth2me.essentials.DescParseTickFormat;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class KeywordReplacer implements IText
{
	private final transient IText input;
	private final transient List<String> replaced;
	private final transient IEssentials ess;
	
	public KeywordReplacer(final IText input, final CommandSender sender, final IEssentials ess)
	{
		this.input = input;
		this.replaced = new ArrayList<String>(this.input.getLines().size());
		this.ess = ess;
		replaceKeywords(sender);
	}
	
	private void replaceKeywords(final CommandSender sender)
	{
		String displayName, ipAddress, balance, mails, world;
		String worlds, online, unique, playerlist, date, time;
		String worldTime12, worldTime24, worldDate, plugins;
		String userName, address, version;
		if (sender instanceof Player)
		{
			final User user = ess.getUser(sender);
			user.setDisplayNick();
			displayName = user.getDisplayName();
			userName = user.getName();
			ipAddress = user.getAddress() == null || user.getAddress().getAddress() == null ? "" : user.getAddress().getAddress().toString();
			address = user.getAddress() == null ? "" : user.getAddress().toString();
			balance = Util.displayCurrency(user.getMoney(), ess);
			mails = Integer.toString(user.getMails().size());
			world = user.getLocation() == null || user.getLocation().getWorld() == null ? "" : user.getLocation().getWorld().getName();
			worldTime12 = DescParseTickFormat.format12(user.getWorld() == null ? 0 : user.getWorld().getTime());
			worldTime24 = DescParseTickFormat.format24(user.getWorld() == null ? 0 : user.getWorld().getTime());
			worldDate = DateFormat.getDateInstance(DateFormat.MEDIUM, ess.getI18n().getCurrentLocale()).format(DescParseTickFormat.ticksToDate(user.getWorld() == null ? 0 : user.getWorld().getFullTime()));
		}
		else
		{
			displayName = ipAddress = balance = mails = world = worldTime12 = worldTime24 = worldDate = "";
		}

		int playerHidden = 0;
		for (Player p : ess.getServer().getOnlinePlayers())
		{
			if (ess.getUser(p).isHidden())
			{
				playerHidden++;
			}
		}
		online = Integer.toString(ess.getServer().getOnlinePlayers().length - playerHidden);
		unique = Integer.toString(ess.getUserMap().getUniqueUsers());

		final StringBuilder worldsBuilder = new StringBuilder();
		for (World w : ess.getServer().getWorlds())
		{
			if (worldsBuilder.length() > 0)
			{
				worldsBuilder.append(", ");
			}
			worldsBuilder.append(w.getName());
		}
		worlds = worldsBuilder.toString();

		final StringBuilder playerlistBuilder = new StringBuilder();
		for (Player p : ess.getServer().getOnlinePlayers())
		{
			if (ess.getUser(p).isHidden())
			{
				continue;
			}
			if (playerlistBuilder.length() > 0)
			{
				playerlistBuilder.append(", ");
			}
			playerlistBuilder.append(p.getDisplayName());
		}
		playerlist = playerlistBuilder.toString();

		final StringBuilder pluginlistBuilder = new StringBuilder();
		for (Plugin p : ess.getServer().getPluginManager().getPlugins())
		{
			if (pluginlistBuilder.length() > 0)
			{
				pluginlistBuilder.append(", ");
			}
			pluginlistBuilder.append(p.getDescription().getName());
		}
		plugins = pluginlistBuilder.toString();

		date = DateFormat.getDateInstance(DateFormat.MEDIUM, ess.getI18n().getCurrentLocale()).format(new Date());
		time = DateFormat.getTimeInstance(DateFormat.MEDIUM, ess.getI18n().getCurrentLocale()).format(new Date());

		version = ess.getServer().getVersion();

		for (int i = 0; i < input.getLines().size(); i++)
		{
			String line = input.getLines().get(i);

			line = line.replace("{PLAYER}", displayName);
			line = line.replace("{DISPLAYNAME}", displayName);
			line = line.replace("{USERNAME}", displayName);
			line = line.replace("{IP}", ipAddress);
			line = line.replace("{ADDRESS}", ipAddress);
			line = line.replace("{BALANCE}", balance);
			line = line.replace("{MAILS}", mails);
			line = line.replace("{WORLD}", world);
			line = line.replace("{ONLINE}", online);
			line = line.replace("{UNIQUE}", unique);
			line = line.replace("{WORLDS}", worlds);
			line = line.replace("{PLAYERLIST}", playerlist);
			line = line.replace("{TIME}", time);
			line = line.replace("{DATE}", date);
			line = line.replace("{WORLDTIME12}", worldTime12);
			line = line.replace("{WORLDTIME24}", worldTime24);
			line = line.replace("{WORLDDATE}", worldDate);
			line = line.replace("{PLUGINS}", plugins);
			line = line.replace("{VERSION}", version);
			replaced.add(line);
		}
	}

	@Override
	public List<String> getLines()
	{
		return replaced;
	}

	@Override
	public List<String> getChapters()
	{
		return input.getChapters();
	}

	@Override
	public Map<String, Integer> getBookmarks()
	{
		return input.getBookmarks();
	}
}
