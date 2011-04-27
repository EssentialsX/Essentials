package com.earth2me.essentials.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.TargetBlock;
import com.earth2me.essentials.User;
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

		World world = user.getWorld();
		if (args.length < 1)
		{
			world.strikeLightning(user.getTargetBlock(null, 600).getLocation());
			return;
		}

		if(server.matchPlayer(args[0]).isEmpty())
		{
			user.sendMessage("§cPlayer not found");
			return;
		}

		for (Player p : server.matchPlayer(args[0]))
		{
			user.sendMessage("§7Smiting " + p.getDisplayName());
			world.strikeLightning(p.getLocation());
			p.setHealth(p.getHealth() < 5 ? 0 : p.getHealth() -5);
			p.sendMessage("§7You have just been smited");
		}
		user.charge(this);
	}
}
