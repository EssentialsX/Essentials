package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.StringUtil;
import com.earth2me.essentials.textreader.SimpleTextInput;
import com.earth2me.essentials.textreader.TextPager;
import com.earth2me.essentials.utils.NumberUtil;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

//TODO: Remove op and replace with perm
public class Commandbalancetop extends EssentialsCommand
{
	public Commandbalancetop()
	{
		super("balancetop");
	}
	private static final int CACHETIME = 2 * 60 * 1000;
	public static final int MINUSERS = 50;
	private static SimpleTextInput cache = new SimpleTextInput();
	private static long cacheage = 0;
	private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		int page = 0;
		boolean force = false;
		if (args.length > 0)
		{
			try
			{
				page = Integer.parseInt(args[0]);
			}
			catch (NumberFormatException ex)
			{
				if (args[0].equalsIgnoreCase("force") && sender.isOp())
				{
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
					outputCache(sender, page);
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
			ess.runTaskAsynchronously(new Viewer(sender, page, force));
		}
		else
		{
			if (ess.getUserMap().getUniqueUsers() > MINUSERS)
			{
				sender.sendMessage(_("orderBalances", ess.getUserMap().getUniqueUsers()));
			}
			ess.runTaskAsynchronously(new Viewer(sender, page, force));
		}

	}

	private static void outputCache(final CommandSender sender, int page)
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(cacheage);
		final DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		sender.sendMessage(_("balanceTop", format.format(cal.getTime())));
		new TextPager(cache).showPage(Integer.toString(page), null, "balancetop", sender);
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
					cache.getLines().clear();
					final Map<String, BigDecimal> balances = new HashMap<String, BigDecimal>();
					BigDecimal totalMoney = BigDecimal.ZERO;
					for (String u : ess.getUserMap().getAllUniqueUsers())
					{
						final User user = ess.getUserMap().getUser(u);
						if (user != null)
						{
							final BigDecimal userMoney = user.getMoney();
							user.updateMoneyCache(userMoney);
							totalMoney = totalMoney.add(userMoney);
							final String name = user.isHidden() ? user.getName() : user.getDisplayName();
							balances.put(name, userMoney);
						}
					}

					final List<Map.Entry<String, BigDecimal>> sortedEntries = new ArrayList<Map.Entry<String, BigDecimal>>(balances.entrySet());
					Collections.sort(sortedEntries, new Comparator<Map.Entry<String, BigDecimal>>()
					{
						@Override
						public int compare(final Entry<String, BigDecimal> entry1, final Entry<String, BigDecimal> entry2)
						{
							return entry2.getValue().compareTo(entry1.getValue());
						}
					});

					cache.getLines().add(_("serverTotal", NumberUtil.displayCurrency(totalMoney, ess)));
					int pos = 1;
					for (Map.Entry<String, BigDecimal> entry : sortedEntries)
					{
						cache.getLines().add(pos + ". " + entry.getKey() + ", " + NumberUtil.displayCurrency(entry.getValue(), ess));
						pos++;
					}
					cacheage = System.currentTimeMillis();
				}
			}
			finally
			{
				lock.writeLock().unlock();
			}
			ess.runTaskAsynchronously(viewer);
		}
	}


	private class Viewer implements Runnable
	{
		private final transient CommandSender sender;
		private final transient int page;
		private final transient boolean force;

		public Viewer(final CommandSender sender, final int page, final boolean force)
		{
			this.sender = sender;
			this.page = page;
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
					outputCache(sender, page);
					return;
				}
			}
			finally
			{
				lock.readLock().unlock();
			}
			ess.runTaskAsynchronously(new Calculator(new Viewer(sender, page, false), force));
		}
	}
}
