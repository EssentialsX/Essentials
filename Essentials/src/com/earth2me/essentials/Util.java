package com.earth2me.essentials;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;


public class Util
{
	public static String sanitizeFileName(String name)
	{
		return name.toLowerCase().replaceAll("[^a-z0-9]", "_");
	}

	public static String formatDateDiff(long date)
	{
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(date);
		Calendar now = new GregorianCalendar();
		return Util.formatDateDiff(now, c);
	}
	
	public static String formatDateDiff(Calendar fromDate, Calendar toDate)
	{
		boolean future = false;
		if (toDate.equals(fromDate))
		{
			return "now";
		}
		if (toDate.after(fromDate))
		{
			future = true;
		}

		StringBuilder sb = new StringBuilder();
		int[] types = new int[]
		{
			Calendar.YEAR,
			Calendar.MONTH,
			Calendar.DAY_OF_MONTH,
			Calendar.HOUR_OF_DAY,
			Calendar.MINUTE,
			Calendar.SECOND
		};
		String[] names = new String[]
		{
			"year",
			"month",
			"day",
			"hour",
			"minute",
			"second"
		};
		for (int i = 0; i < types.length; i++)
		{
			int diff = dateDiff(types[i], fromDate, toDate, future);
			if (diff > 0)
			{
				sb.append(" ").append(diff).append(" ").append(names[i]);
				if (diff > 1)
				{
					sb.append("s");
				}
			}
		}
		if (sb.length() == 0)
		{
			return "now";
		}
		return sb.toString();
	}

	private static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future)
	{
		int diff = 0;
		while ((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate)))
		{
			fromDate.add(type, future ? 1 : -1);
			diff++;
		}
		diff--;
		fromDate.add(type, future ? -1 : 1);
		return diff;
	}
	private static Pattern timePattern = Pattern.compile(
			"(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?"
			+ "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
			+ "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?"
			+ "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
			+ "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?"
			+ "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?"
			+ "(?:([0-9]+)\\s*s[a-z]*)?", Pattern.CASE_INSENSITIVE);

	public static long parseDateDiff(String time, boolean future) throws Exception
	{
		Matcher m = timePattern.matcher(time);
		int years = 0;
		int months = 0;
		int weeks = 0;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		boolean found = false;
		while (m.find())
		{
			if (m.group() == null || m.group().isEmpty())
			{
				continue;
			}
			for (int i = 0; i < m.groupCount(); i++)
			{
				if (m.group(i) != null && !m.group(i).isEmpty())
				{
					found = true;
					break;
				}
			}
			if (found)
			{
				if (m.group(1) != null && !m.group(1).isEmpty())
				{
					years = Integer.parseInt(m.group(1));
				}
				if (m.group(2) != null && !m.group(2).isEmpty())
				{
					months = Integer.parseInt(m.group(2));
				}
				if (m.group(3) != null && !m.group(3).isEmpty())
				{
					weeks = Integer.parseInt(m.group(3));
				}
				if (m.group(4) != null && !m.group(4).isEmpty())
				{
					days = Integer.parseInt(m.group(4));
				}
				if (m.group(5) != null && !m.group(5).isEmpty())
				{
					hours = Integer.parseInt(m.group(5));
				}
				if (m.group(6) != null && !m.group(6).isEmpty())
				{
					minutes = Integer.parseInt(m.group(6));
				}
				if (m.group(7) != null && !m.group(7).isEmpty())
				{
					seconds = Integer.parseInt(m.group(7));
				}
				break;
			}
		}
		if (!found)
		{
			throw new Exception("Illegal date format.");
		}
		Calendar c = new GregorianCalendar();
		if (years > 0)
		{
			c.add(Calendar.YEAR, years * (future ? 1 : -1));
		}
		if (months > 0)
		{
			c.add(Calendar.MONTH, months * (future ? 1 : -1));
		}
		if (weeks > 0)
		{
			c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
		}
		if (days > 0)
		{
			c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : -1));
		}
		if (hours > 0)
		{
			c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
		}
		if (minutes > 0)
		{
			c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
		}
		if (seconds > 0)
		{
			c.add(Calendar.SECOND, seconds * (future ? 1 : -1));
		}
		return c.getTimeInMillis();
	}
	
	public static Location getSafeDestination(Location loc) throws Exception
	{
		if (loc == null)
		{
			throw new Exception("Destination not set");
		}
		World world = loc.getWorld();
		double x = Math.floor(loc.getX()) + 0.5;
		double y = Math.floor(loc.getY());
		double z = Math.floor(loc.getZ()) + 0.5;

		while (isBlockAboveAir(world, x, y, z))
		{
			y -= 1.0D;
			if (y < 0.0D)
			{
				throw new Exception("Hole in floor");
			}
		}

		while (isBlockUnsafe(world, x, y, z))
		{
			y += 1.0D;
			if (y >= 110.0D)
			{
				x += 1.0D;
				break;
			}
		}
		while (isBlockUnsafe(world, x, y, z))
		{
			y -= 1.0D;
			if (y <= 1.0D)
			{
				y = 110.0D;
				x += 1.0D;
			}
		}
		return new Location(world, x, y, z, loc.getYaw(), loc.getPitch());
	}

	private static boolean isBlockAboveAir(World world, double x, double y, double z)
	{
		return world.getBlockAt((int)Math.floor(x), (int)Math.floor(y - 1.0D), (int)Math.floor(z)).getType() == Material.AIR;
	}

	public static boolean isBlockUnsafe(World world, double x, double y, double z)
	{
		Block below = world.getBlockAt((int)Math.floor(x), (int)Math.floor(y - 1.0D), (int)Math.floor(z));
		if (below.getType() == Material.LAVA || below.getType() == Material.STATIONARY_LAVA)
		{
			return true;
		}

		if (below.getType() == Material.FIRE)
		{
			return true;
		}

		if ((world.getBlockAt((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z)).getType() != Material.AIR)
			|| (world.getBlockAt((int)Math.floor(x), (int)Math.floor(y + 1.0D), (int)Math.floor(z)).getType() != Material.AIR))
		{
			return true;
		}
		return isBlockAboveAir(world, x, y, z);
	}
}
