package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Util;
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
	
	private static final int CACHETIME = 5 * 60 * 1000;
	public static final int MINUSERS = 50;
	private static List<String> cache = new ArrayList<String>();
	private static long cacheage = 0;
	private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		int max = 10;
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
				//catch it because they tried to enter a string not number.
			}
		}

		if (lock.readLock().tryLock())
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
			ess.scheduleAsyncDelayedTask(new Viewer(sender, max));
		}
		else
		{
			if (ess.getUserMap().getUniqueUsers() > MINUSERS)
			{
				sender.sendMessage(_("orderBalances", ess.getUserMap().getUniqueUsers()));
			}
			ess.scheduleAsyncDelayedTask(new Viewer(sender, max));
		}

	}

	private static void outputCache(final CommandSender sender, int max)
	{
		sender.sendMessage(_("balanceTop", max));
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

		public Calculator(final Viewer viewer)
		{
			this.viewer = viewer;
		}

		@Override
		public void run()
		{
			lock.writeLock().lock();
			try
			{
				if (cacheage < System.currentTimeMillis() - 5 * 60 * 1000)
				{
					final Map<String, Double> balances = new HashMap<String, Double>();
					for (String u : ess.getUserMap().getAllUniqueUsers())
					{
						try
						{
							balances.put(u, ess.getUserMap().getUser(u).getMoney());
						}
						catch (NullPointerException ex)
						{
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

		public Viewer(final CommandSender sender, final int max)
		{
			this.sender = sender;
			this.max = max;
		}

		@Override
		public void run()
		{
			lock.readLock().lock();
			try
			{
				if (cacheage > System.currentTimeMillis() - 5 * 60 * 1000)
				{
					outputCache(sender, max);
					return;
				}
			}
			finally
			{
				lock.readLock().unlock();
			}
			ess.scheduleAsyncDelayedTask(new Calculator(new Viewer(sender, max)));
		}
	}
}
