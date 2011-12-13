package com.earth2me.essentials.craftbukkit;

import org.bukkit.entity.Player;


public class SetExpFix
{
	public static void setTotalExperience(final Player player, final int exp)
	{
		if (exp < 0)
		{
			throw new IllegalArgumentException("Experience is negative!");
		}
		player.setExp(0);
		player.setLevel(0);
		player.setTotalExperience(0);
		int amount = exp;
		while (amount > 0)
		{
			final int expToLevel = getExpTolevel(player);
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

	private static int getExpTolevel(final Player player)
	{
		return 7 + (player.getLevel() * 7 >> 1);
	}
}
