package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.List;


public class Commandhome extends EssentialsCommand
{
	public Commandhome()
	{
		super("home");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		Trade charge = new Trade(this.getName(), ess);
		charge.isAffordableFor(user);
		User u = user;
		String homeName = "";
		String[] nameParts;
		if (args.length > 0)
		{
			nameParts = args[0].split(":");
			if (nameParts[0].length() == args[0].length() || !user.isAuthorized("essentials.home.others"))
			{
				homeName = nameParts[0];
			}
			else
			{
				u = getPlayer(server, nameParts[0].split(" "), 0, true);
				if (nameParts.length > 1)
				{
					homeName = nameParts[1];
				}
			}
		}
		try
		{
			user.getTeleport().home(u, homeName.toLowerCase(), charge);
		}
		catch (NotEnoughArgumentsException e)
		{
			List<String> homes = u.getHomes();
			if (homes.isEmpty())
			{
				throw new Exception(u == user ? Util.i18n("noHomeSet") : Util.i18n("noHomeSetPlayer"));
			}
			else if ((homes.size() == 1) && u == user)
			{
				user.getTeleport().home(u, homes.get(0), charge);
			}
			else if (ess.getSettings().spawnIfNoHome())
			{
				user.getTeleport().respawn(ess.getSpawn(), charge);				
			}
			else
			{
				user.sendMessage(Util.format("homes", Util.joinList(homes)));
			}
		}
		throw new NoChargeException();
	}
}
