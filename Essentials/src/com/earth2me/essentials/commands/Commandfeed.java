package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.entity.Player;


public class Commandfeed extends EssentialsCommand
{
	public Commandfeed()
	{
		super("feed");
	}

	@Override
	protected void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length > 0)
		{
			Player player = ess.getServer().getPlayer(args[0]);
			if (player != null)
			{
				player.setFoodLevel(20);
				player.setSaturation(10);
			}
			else
			{
				throw new NotEnoughArgumentsException(); // TODO: Translate "Player not found"
			}
		}
		else
		{
			user.setFoodLevel(20);
			user.setSaturation(10); // 10 because 20 seems way overpowered
		}
	}
}
