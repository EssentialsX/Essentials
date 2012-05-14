package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.craftbukkit.SetExpFix;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commandexp extends EssentialsCommand
{
	public Commandexp()
	{
		super("exp");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args)throws Exception
	{
		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			final User user = ess.getUser(onlinePlayer);
			{
				if (args.length < 1 && user.isAuthorized("essentials.exp"))
				{
					int totalexp = SetExpFix.getTotalExperience(user);
					int expleft = (int)Util.roundDouble(((((3.5 * user.getLevel()) + 6.7) - (totalexp - ((1.75 *(user.getLevel() * user.getLevel())) + (5.00 * user.getLevel())))) + 1));
					sender.sendMessage(_("exp", SetExpFix.getTotalExperience(user), expleft));
				}
				if (args.length == 1 && user.isAuthorized("essentials.exp.others"))
				{
					for (Player p : server.matchPlayer(args[0]))
					{
						User player = getPlayer(server, args, 1);
						int totalexp = SetExpFix.getTotalExperience(user);
						int expleft = (int)Util.roundDouble(((((3.5 * user.getLevel()) + 6.7) - (totalexp - ((1.75 *(user.getLevel() * user.getLevel())) + (5.00 * user.getLevel())))) + 1));
					sender.sendMessage(_("expothers", player.getDisplayName(), SetExpFix.getTotalExperience(p), expleft));
					}
				}
				if(args.length >= 1)
					if (args[0].equalsIgnoreCase("set"))
					{
						if (args.length >= 2)
							for (Player p : server.matchPlayer(args[1]))
								if ((args.length == 3) && (user.isAuthorized("essentials.exp.set.others")))
								{
									User player = getPlayer(server, args, 1);
									int exp = Integer.parseInt(args[2]);
									SetExpFix.setTotalExperience(p, exp);
									sender.sendMessage(_("expsetothers", player.getDisplayName(), exp));
								}
								else if ((args.length == 2) && (user.isAuthorized("essentials.exp.set")))
								{
									int exp = Integer.parseInt(args[1]);
									SetExpFix.setTotalExperience(user, exp);
									sender.sendMessage(_("expset", exp));
								}
						
						
					}
					else if (args[0].equalsIgnoreCase("give"))
					{
						if (args.length >= 2)
							for (Player p : server.matchPlayer(args[1]))
								if ((args.length == 3) && (user.isAuthorized("essentials.exp.give.others")))
								{
									User player = getPlayer(server, args, 1);
									int amount = Integer.parseInt(args[2]);
									p.giveExp(amount);
									sender.sendMessage(_("expgiveothers", player.getDisplayName(), amount));
								}
								else if ((args.length == 2) && (user.isAuthorized("essentials.exp.give")))
								{
									int amount = Integer.parseInt(args[1]);
									user.giveExp(amount);
									sender.sendMessage(_("expgive", amount));
								}

					}
				
			}
		}	
	}
}
