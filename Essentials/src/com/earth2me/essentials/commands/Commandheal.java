package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;


public class Commandheal extends EssentialsCommand
{
	public Commandheal()
	{
		super("heal");
	}

	@Override
	public String[] getTriggers() {
		return new String[] {
			getName(), "eheal"
		};
	}
	
	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length > 0 && user.isAuthorized("essentials.heal.others"))
		{
			if (!user.isAuthorized("essentials.heal.cooldown.bypass")) user.healCooldown();
			user.charge(this);
			for (Player p : server.matchPlayer(args[0]))
			{
				p.setHealth(20);
				user.sendMessage("§7Healed " + p.getDisplayName() + ".");
			}
			return;
		}
		
		if (!user.isAuthorized("essentials.heal.cooldown.bypass")) user.healCooldown();
		user.charge(this);
		user.setHealth(20);
		user.sendMessage("§7You have been healed.");
	}

	@Override
	public void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			sender.sendMessage("Usage: /" + commandLabel + " [player]");
			return;
		}

		for (Player p : server.matchPlayer(args[0]))
		{
			p.setHealth(20);
			sender.sendMessage("Healed " + p.getDisplayName() + ".");
		}
	}
}
