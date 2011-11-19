package com.earth2me.essentials.textreader;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import java.util.List;
import java.util.Map;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class KeywordReplacer implements IText
{
	private final transient IText input;
	private final transient IEssentials ess;

	public KeywordReplacer(final IText input, final CommandSender sender, final IEssentials ess)
	{
		this.input = input;
		this.ess = ess;
		replaceKeywords(sender);
	}

	private void replaceKeywords(final CommandSender sender)
	{
		String displayName, ipAddress, balance, mails, world;
		String worlds, online, unique, playerlist;
		if (sender instanceof Player)
		{
			final User user = ess.getUser(sender);
			displayName = user.getDisplayName();
			ipAddress = user.getAddress().getAddress().toString();
			balance = Double.toString(user.getMoney());
			mails = Integer.toString(user.getMails().size());
			world = user.getLocation().getWorld().getName();
		}
		else
		{
			displayName = ipAddress = balance = mails = world = "";
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

		for (int i = 0; i < input.getLines().size(); i++)
		{
			String line = input.getLines().get(i);
			line = line.replace("{PLAYER}", displayName);
			line = line.replace("{IP}", ipAddress);
			line = line.replace("{BALANCE}", balance);
			line = line.replace("{MAILS}", mails);
			line = line.replace("{WORLD}", world);
			line = line.replace("{ONLINE}", online);
			line = line.replace("{UNIQUE}", unique);
			line = line.replace("{WORLDS}", worlds);
			line = line.replace("{PLAYERLIST}", playerlist);
			input.getLines().set(i, line);
		}
	}

	@Override
	public List<String> getLines()
	{
		return input.getLines();
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
