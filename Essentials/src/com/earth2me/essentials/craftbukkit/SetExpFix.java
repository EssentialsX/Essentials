package com.earth2me.essentials.craftbukkit;

import org.bukkit.entity.Player;


public class SetExpFix
{
	//This method is used to update both the recorded total experience and displayed total experience.
	//We reset both types to prevent issues.
	public static void setTotalExperience(final Player player, final int exp)
	{
		if (exp < 0)
		{
			throw new IllegalArgumentException("Experience is negative!");
		}
		player.setExp(0);
		player.setLevel(0);
		player.setTotalExperience(0);

		//This following code is technically redundant now, as bukkit now calulcates levels more or less correctly
		//At larger numbers however... player.getExp(3000), only seems to give 2999, putting the below calculations off.
		int amount = exp;
		while (amount > 0)
		{
			final int expToLevel = getExpToLevel(player);
			amount -= expToLevel;
			if (amount >= 0)
			{
				// give until next level
				player.giveExp(expToLevel);
			}
			else
			{
				// give the rest
				amount += expToLevel;
				player.giveExp(amount);
				amount = 0;
			}
		}
	}

	private static int getExpToLevel(final Player player)
	{
		return getExpToLevel(player.getLevel());
	}

	private static int getExpToLevel(final int level)
	{
		if (level >= 30)
		{
			return 62 + (level - 30) * 7;
		}
		if (level >= 15)
		{
			return 17 + (level - 15) * 3;
		}
		return 17;
	}

	//This method is required because the bukkit player.getTotalExperience() method, shows exp that has been 'spent'.
	//Without this people would be able to use exp and then still sell it.
	public static int getTotalExperience(final Player player)
	{
		int exp = (int)Math.round(getExpToLevel(player) * player.getExp());
		int currentLevel = player.getLevel();

		while (currentLevel > 0)
		{
			currentLevel--;
			exp += getExpToLevel(currentLevel);
		}
		return exp;
	}
	
	public static int getExpUntilNextLevel(final Player player)
	{
		int exp = 0;
		int currentLevel = player.getLevel() + 1;

		while (currentLevel > 0)
		{
			currentLevel--;
			exp += getExpToLevel(currentLevel);
		}
		return exp - getTotalExperience(player);
	}
}
