package com.earth2me.essentials.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class Commandlightning extends EssentialsCommand
{
	public Commandlightning()
	{
		super("lightning");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{

		if (args.length < 1)
		{
			user.sendMessage("§cUsage: /" + commandLabel + " [player]");
			return;
		}

		World world = user.getWorld();
		if(server.matchPlayer(args[0]).isEmpty())
		{
			user.sendMessage("§cPlayer not found");
			return;
		}

		for (Player p : server.matchPlayer(args[0]))
		{
			user.sendMessage("§7Smiting" + p.getDisplayName());
			world.strikeLightning(p.getLocation());
			p.setHealth(0);
			p.sendMessage("§7You have just been smited");
		}
		user.charge(this);
	}
}
