package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;
import java.util.List;


public class Commandheal extends EssentialsCommand
{
	public Commandheal()
	{
		super("heal");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{

		if (args.length > 0 && user.isAuthorized("essentials.heal.others"))
		{
			if (!user.isAuthorized("essentials.heal.cooldown.bypass"))
			{
				user.healCooldown();
			}
			charge(user);
			healOtherPlayers(server, user, args[0]);
			return;
		}

		if (!user.isAuthorized("essentials.heal.cooldown.bypass"))
		{
			user.healCooldown();
		}
		charge(user);
		user.setHealth(20);
		user.sendMessage("§7You have been healed.");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		healOtherPlayers(server, sender, args[0]);
	}

	private void healOtherPlayers(Server server, CommandSender sender, String name)
	{
		List<Player> players = server.matchPlayer(name);
		if(players.isEmpty())
		{
			sender.sendMessage("§cPlayer matching " + name + " not found");
			return;
		}
		for (Player p : players)
		{
			p.setHealth(20);
			sender.sendMessage("§7Healed " + p.getDisplayName() + ".");
		}
	}
}
