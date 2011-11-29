package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Util;
import java.text.DateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandbalancetop extends EssentialsCommand
{
	public Commandbalancetop()
	{
		super("balancetop");
	}
	private static final int CACHETIME = 2 * 60 * 1000;
	public static final int MINUSERS = 50;
	private static List<String> cache = new ArrayList<String>();
	private static long cacheage = 0;
	private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		int max = 10;
		boolean force = false;
		if (args.length > 0)
		{
			try
			{
				if (Integer.parseInt(args[0]) < 19)
				{
					max = Integer.parseInt(args[0]);
				}
			}
			catch (NumberFormatException ex)
			{
				if (args[0].equalsIgnoreCase("force") && sender.isOp()) {
					force = true;
				}
			}
		}

		if (!force && lock.readLock().tryLock())
		{
			try
			{
				if (cacheage > System.currentTimeMillis() - CACHETIME)
				{
					outputCache(sender, max);
					return;
				}
				if (ess.getUserMap().getUniqueUsers() > MINUSERS)
				{
					sender.sendMessage(_("orderBalances", ess.getUserMap().getUniqueUsers()));
				}
			}
			finally
			{
				lock.readLock().unlock();
			}
			ess.scheduleAsyncDelayedTask(new Viewer(sender, max, force));
		}
		else
		{
			if (ess.getUserMap().getUniqueUsers() > MINUSERS)
			{
				sender.sendMessage(_("orderBalances", ess.getUserMap().getUniqueUsers()));
			}
			ess.scheduleAsyncDelayedTask(new Viewer(sender, max, force));
		}

	}

	private static void outputCache(final CommandSender sender, int max)
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(cacheage);
		final DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		sender.sendMessage(_("balanceTop", max, format.format(cal.getTime())));
		for (String line : cache)
		{
			if (max == 0)
			{
				break;
			}
			max--;
			sender.sendMessage(line);
		}
	}


	private class Calculator implements Runnable
	{
		private final transient Viewer viewer;
		private final boolean force;

		public Calculator(final Viewer viewer, final boolean force)
		{
			this.viewer = viewer;
			this.force = force;
		}

		@Override
		public void run()
		{
			lock.writeLock().lock();
			try
			{
				if (force || cacheage <= System.currentTimeMillis() - CACHETIME)
				{
					cache.clear();
					final Map<String, Double> balances = new HashMap<String, Double>();
					for (String u : ess.getUserMap().getAllUniqueUsers())
					{
						final User user = ess.getUserMap().getUser(u);
						if (user != null)
						{
							balances.put(u, user.getMoney());
						}
					}

					final List<Map.Entry<String, Double>> sortedEntries = new ArrayList<Map.Entry<String, Double>>(balances.entrySet());
					Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Double>>()
					{
						@Override
						public int compare(final Entry<String, Double> entry1, final Entry<String, Double> entry2)
						{
							return -entry1.getValue().compareTo(entry2.getValue());
						}
					});
					int count = 0;
					for (Map.Entry<String, Double> entry : sortedEntries)
					{
						if (count == 20)
						{
							break;
						}
						cache.add(entry.getKey() + ", " + Util.formatCurrency(entry.getValue(), ess));
						count++;
					}
					cacheage = System.currentTimeMillis();
				}
			}
			finally
			{
				lock.writeLock().unlock();
			}
			ess.scheduleAsyncDelayedTask(viewer);
		}
	}


	private class Viewer implements Runnable
	{
		private final transient CommandSender sender;
		private final transient int max;
		private final transient boolean force;

		public Viewer(final CommandSender sender, final int max, final boolean force)
		{
			this.sender = sender;
			this.max = max;
			this.force = force;
		}

		@Override
		public void run()
		{
			lock.readLock().lock();
			try
			{
				if (!force && cacheage > System.currentTimeMillis() - CACHETIME)
				{
					outputCache(sender, max);
					return;
				}
			}
			finally
			{
				lock.readLock().unlock();
			}
			ess.scheduleAsyncDelayedTask(new Calculator(new Viewer(sender, max, force), force));
		}
	}
}
