package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;


public class Commandhelpop extends EssentialsCommand
{
	public Commandhelpop()
	{
		super("helpop");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage("§cTo request help from an op, type §f/" + commandLabel+ "§c, followed by your question.");
			return;
		}

		user.charge(this);
		for (Player p : server.getOnlinePlayers())
		{
			User u = User.get(p);
			if (!u.isOp() && !u.isAuthorized("essentials.helpop.receive")) continue;
			u.sendMessage("§c[HelpOp]§f §7" + user.getDisplayName() + ":§f " + getFinalArg(args, 0));
		}
	}
}
