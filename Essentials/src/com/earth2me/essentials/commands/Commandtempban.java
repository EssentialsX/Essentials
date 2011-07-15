package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandtempban extends EssentialsCommand
{
	public Commandtempban()
	{
		super("tempban");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		User p = null;
		try
		{
			p = getPlayer(server, args, 0);
		}
		catch (NoSuchFieldException ex)
		{
			p = ess.getOfflineUser(args[0]);
		}
		if (p == null)
		{
			sender.sendMessage(Util.format("playerNotFound"));
		}

		String time = getFinalArg(args, 1);
		long banTimestamp = Util.parseDateDiff(time, true);

		String banReason = Util.format("tempBanned",  Util.formatDateDiff(banTimestamp));
		p.setBanReason(banReason);
		p.setBanTimeout(banTimestamp);
		p.kickPlayer(banReason);
		((CraftServer)server).getHandle().a(p.getName());
		server.broadcastMessage(Util.format("playerBanned", p.getName(), banReason));
		Essentials.getStatic().loadBanList();
	}
}
