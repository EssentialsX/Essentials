package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.UserMap;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.metrics.Metrics;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

// This command has 4 undocumented behaviours #EasterEgg
public class Commandessentials extends EssentialsCommand
{
	public Commandessentials()
	{
		super("essentials");
	}
	private transient int taskid;
	private final transient Map<Player, Block> noteBlocks = new HashMap<Player, Block>();

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length == 0)
		{
			run_disabled(server, sender, commandLabel, args);
		}
		else if (args[0].equalsIgnoreCase("debug"))
		{
			run_debug(server, sender, commandLabel, args);
		}
		else if (args[0].equalsIgnoreCase("nya"))
		{
			run_nya(server, sender, commandLabel, args);
		}
		else if (args[0].equalsIgnoreCase("moo"))
		{
			run_moo(server, sender, commandLabel, args);
		}
		else if (args[0].equalsIgnoreCase("reset"))
		{
			run_reset(server, sender, commandLabel, args);
		}
		else if (args[0].equalsIgnoreCase("opt-out"))
		{
			run_optout(server, sender, commandLabel, args);
		}
		else if (args[0].equalsIgnoreCase("cleanup"))
		{
			run_cleanup(server, sender, commandLabel, args);
		}
		else
		{
			run_reload(server, sender, commandLabel, args);
		}
	}

	//If you do not supply an argument this command will list 'overridden' commands.
	private void run_disabled(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		sender.sendMessage("Essentials " + ess.getDescription().getVersion());
		sender.sendMessage("/<command> <reload/debug>");

		final StringBuilder disabledCommands = new StringBuilder();
		for (Map.Entry<String, String> entry : ess.getAlternativeCommandsHandler().disabledCommands().entrySet())
		{
			if (disabledCommands.length() > 0)
			{
				disabledCommands.append(", ");
			}
			disabledCommands.append(entry.getKey()).append(" => ").append(entry.getValue());
		}
		if (disabledCommands.length() > 0)
		{
			sender.sendMessage(_("blockList"));
			sender.sendMessage(disabledCommands.toString());
		}
	}

	private void run_reset(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new Exception("/<command> reset <player>");
		}
		final User user = getPlayer(server, args, 1, true);
		user.reset();
		sender.sendMessage("Reset Essentials userdata for player: " + user.getDisplayName());
	}

	private void run_debug(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		ess.getSettings().setDebug(!ess.getSettings().isDebug());
		sender.sendMessage("Essentials " + ess.getDescription().getVersion() + " debug mode " + (ess.getSettings().isDebug() ? "enabled" : "disabled"));
	}

	private void run_reload(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		ess.reload();
		sender.sendMessage(_("essentialsReload", ess.getDescription().getVersion()));
	}

	private void run_nya(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		final Map<String, Float> noteMap = new HashMap<String, Float>();
		noteMap.put("1F#", 0.5f);
		noteMap.put("1G", 0.53f);
		noteMap.put("1G#", 0.56f);
		noteMap.put("1A", 0.6f);
		noteMap.put("1A#", 0.63f);
		noteMap.put("1B", 0.67f);
		noteMap.put("1C", 0.7f);
		noteMap.put("1C#", 0.76f);
		noteMap.put("1D", 0.8f);
		noteMap.put("1D#", 0.84f);
		noteMap.put("1E", 0.9f);
		noteMap.put("1F", 0.94f);
		noteMap.put("2F#", 1.0f);
		noteMap.put("2G", 1.06f);
		noteMap.put("2G#", 1.12f);
		noteMap.put("2A", 1.18f);
		noteMap.put("2A#", 1.26f);
		noteMap.put("2B", 1.34f);
		noteMap.put("2C", 1.42f);
		noteMap.put("2C#", 1.5f);
		noteMap.put("2D", 1.6f);
		noteMap.put("2D#", 1.68f);
		noteMap.put("2E", 1.78f);
		noteMap.put("2F", 1.88f);
		final String tuneStr = "1D#,1E,2F#,,2A#,1E,1D#,1E,2F#,2B,2D#,2E,2D#,2A#,2B,,2F#,,1D#,1E,2F#,2B,2C#,2A#,2B,2C#,2E,2D#,2E,2C#,,2F#,,2G#,,1D,1D#,,1C#,1D,1C#,1B,,1B,,1C#,,1D,,1D,1C#,1B,1C#,1D#,2F#,2G#,1D#,2F#,1C#,1D#,1B,1C#,1B,1D#,,2F#,,2G#,1D#,2F#,1C#,1D#,1B,1D,1D#,1D,1C#,1B,1C#,1D,,1B,1C#,1D#,2F#,1C#,1D,1C#,1B,1C#,,1B,,1C#,,2F#,,2G#,,1D,1D#,,1C#,1D,1C#,1B,,1B,,1C#,,1D,,1D,1C#,1B,1C#,1D#,2F#,2G#,1D#,2F#,1C#,1D#,1B,1C#,1B,1D#,,2F#,,2G#,1D#,2F#,1C#,1D#,1B,1D,1D#,1D,1C#,1B,1C#,1D,,1B,1C#,1D#,2F#,1C#,1D,1C#,1B,1C#,,1B,,1B,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1B,,";
		final String[] tune = tuneStr.split(",");
		taskid = ess.scheduleSyncRepeatingTask(new Runnable()
		{
			int i = 0;

			@Override
			public void run()
			{
				final String note = tune[i];
				i++;
				if (i >= tune.length)
				{
					Commandessentials.this.stopTune();
				}
				if (note.isEmpty() || note == null)
				{
					return;
				}
				for (Player onlinePlayer : server.getOnlinePlayers())
				{
					onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.NOTE_PIANO, 1, noteMap.get(note));
				}
			}
		}, 20, 2);
	}

	private void stopTune()
	{
		ess.getScheduler().cancelTask(taskid);
		for (Block block : noteBlocks.values())
		{
			if (block.getType() == Material.NOTE_BLOCK)
			{
				block.setType(Material.AIR);
			}
		}
		noteBlocks.clear();
	}

	private void run_moo(final Server server, final CommandSender sender, final String command, final String args[])
	{
		if (sender instanceof ConsoleCommandSender)
		{
			sender.sendMessage(new String[]
					{
						"         (__)", "         (oo)", "   /------\\/", "  / |    ||", " *  /\\---/\\", "    ~~   ~~", "....\"Have you mooed today?\"..."
					});
		}
		else
		{
			sender.sendMessage(new String[]
					{
						"            (__)", "            (oo)", "   /------\\/", "  /  |      | |", " *  /\\---/\\", "    ~~    ~~", "....\"Have you mooed today?\"..."
					});
			Player player = (Player)sender;
			player.playSound(player.getLocation(), Sound.COW_IDLE, 1, 1.0f);
		}
	}

	private void run_optout(final Server server, final CommandSender sender, final String command, final String args[])
	{
		final Metrics metrics = ess.getMetrics();
		try
		{
			sender.sendMessage("Essentials collects simple metrics to highlight which features to concentrate work on in the future.");
			if (metrics.isOptOut())
			{
				metrics.enable();
			}
			else
			{
				metrics.disable();
			}
			sender.sendMessage("Anonymous Metrics are now: " + (metrics.isOptOut() ? "disabled" : "enabled"));
		}
		catch (IOException ex)
		{
			sender.sendMessage("Unable to modify 'plugins/PluginMetrics/config.yml': " + ex.getMessage());
		}
	}

	private void run_cleanup(final Server server, final CommandSender sender, final String command, final String args[]) throws Exception
	{
		if (args.length < 2 || !Util.isInt(args[1]))
		{
			sender.sendMessage("This sub-command will delete users who havent logged in in the last <days> days.");
			sender.sendMessage("Optional parameters define the minium amount required to prevent deletion.");
			throw new Exception("/<command> cleanup <days> [money] [homes] [ban count]");
		}
		sender.sendMessage(_("cleaning"));

		final int daysArg = Integer.parseInt(args[1]);
		final double moneyArg = args.length >= 3 ? Double.parseDouble(args[2].replaceAll("[^0-9\\.]", "")) : 0;
		final int homesArg = args.length >= 4 && Util.isInt(args[3]) ? Integer.parseInt(args[3]) : 0;
		final int bansArg = args.length >= 5 && Util.isInt(args[4]) ? Integer.parseInt(args[4]) : 0;
		final UserMap userMap = ess.getUserMap();

		ess.runTaskAsynchronously(new Runnable()
		{
			@Override
			public void run()
			{
				for (String u : userMap.getAllUniqueUsers())
				{
					final User user = ess.getUserMap().getUser(u);
					if (user == null)
					{
						continue;
					}

					int ban = user.getBanReason().equals("") ? 0 : 1;
					long timeDiff = System.currentTimeMillis() - user.getLastLogout();
					long milliDays = daysArg * 24 * 60 * 60;

					if ((ban > bansArg) || (timeDiff < milliDays)
						|| (user.getHomes().size() > homesArg) || (user.getMoney() > moneyArg))
					{
						continue;
					}
					user.reset();
				}
				sender.sendMessage(_("cleaned"));
			}
		});

	}
}
