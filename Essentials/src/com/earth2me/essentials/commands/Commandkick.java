package com.earth2me.essentials.commands;

import com.earth2me.essentials.Console;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.entity.Player;


public class Commandkick extends EssentialsCommand
{
	public Commandkick()
	{
		super("kick");
	}
	
	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		
		User player = getPlayer(server, args, 0);
		if (player.isAuthorized("essentials.kick.exempt"))
		{
			sender.sendMessage(Util.i18n("kickExempt"));
			return;
		}
		charge(sender);
		final String kickReason = args.length > 1 ? getFinalArg(args, 1) : Util.i18n("kickDefault");
		player.kickPlayer(kickReason);
		String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : Console.NAME;
		
		for(Player p : server.getOnlinePlayers())
		{
			User u = ess.getUser(p);
			if(u.isAuthorized("essentials.kick.notify"))
			{
			p.sendMessage(Util.format("playerKicked", senderName, player.getName(), kickReason));
			}
		}
	}
}
