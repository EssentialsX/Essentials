package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.craftbukkit.SetExpFix;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Commandexp extends EssentialsCommand
{
	public Commandexp()
	{
		super("exp");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length == 0)
		{		
			if (user.isAuthorized("essentials.exp"))
			{
				int totalexp = SetExpFix.getTotalExperience(user);
				int expleft = (int)Util.roundDouble(((((3.5 * user.getLevel()) + 6.7) - (totalexp - ((1.75 * (user.getLevel() * user.getLevel())) + (5.00 * user.getLevel())))) + 1));
				user.sendMessage(_("exp", totalexp, expleft));
			}
		}
		else if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("set"))
			{
				if (args.length > 1)
				{
					for (Player p : server.matchPlayer(args[1]))
					{
						if ((args.length == 3) && (args[1].trim().length() > 2) && (user.isAuthorized("essentials.exp.set.others")))
						{
							User player = getPlayer(server, args, 1);
							int amount = Integer.parseInt(args[2]);
							SetExpFix.setTotalExperience(p, amount);
							user.sendMessage(_("expsetothers", player.getDisplayName(), amount));
							p.sendMessage(_("expsetothersreceive", amount));
						}
					}
					if ((args.length == 2) && (user.isAuthorized("essentials.exp.set")))
					{
						int amount = Integer.parseInt(args[1]);
						SetExpFix.setTotalExperience(user, amount);
						user.sendMessage(_("expset", amount));
					}
				}
			}
			if (args[0].equalsIgnoreCase("give"))
			{
				if (args.length > 1)
				{
					for (Player p : server.matchPlayer(args[1]))
					{
						if ((args.length == 3) && (args[1].trim().length() > 2) && (user.isAuthorized("essentials.exp.give.others")))
						{
							User player = getPlayer(server, args, 1);
							int amount = Integer.parseInt(args[2]);
							p.giveExp(amount);
							user.sendMessage(_("expgiveothers", player.getDisplayName(), amount));
							p.sendMessage (_("expgiveothersreceive", amount));
						}
					}
					if ((args.length == 2) && (user.isAuthorized("essentials.exp.give")))
					{
						int amount = Integer.parseInt(args[1]);
						user.giveExp(amount);
						user.sendMessage(_("expgive", amount));
					}

				}
			}
			if (args[0].equalsIgnoreCase("player"))
			{
				if (args.length > 1)
				{
					for (Player p : server.matchPlayer(args[1]))
					{
						if ((args.length == 2) && (args[1].trim().length() > 2) && (user.isAuthorized("essentials.exp.others")))
						{
							User player = getPlayer(server, args, 1);
							int totalexp = SetExpFix.getTotalExperience(p);
							int expleft = (int)Util.roundDouble(((((3.5 * p.getLevel()) + 6.7) - (totalexp - ((1.75 * (player.getLevel() * player.getLevel())) + (5.00 * player.getLevel())))) + 1));
							user.sendMessage(_("expothers", player.getDisplayName(), SetExpFix.getTotalExperience(p), expleft));
						}
					}
				}
			}
			if (args[0].equalsIgnoreCase("fix"))
			{
				{	if ((args.length == 1) && (user.isAuthorized("essentials.exp.fix")))
					{
						if (SetExpFix.getTotalExperience(user) < 0)
						{
							user.sendMessage(_("expfix"));
							user.setExp(0);
							user.setLevel(0);
							user.setTotalExperience(0);
						}
						else if (SetExpFix.getTotalExperience(user) >= 0)
						{
							user.sendMessage(_("expfixfalse"));
						}
					}
					else if ((args.length == 2) && (user.isAuthorized("essentials.exp.fix.others")))
					{
						for (Player p : server.matchPlayer(args[1]))
						{
							if (SetExpFix.getTotalExperience(p) < 0)
							{
								
								user.sendMessage(_("expfixothers", p.getDisplayName()));
								p.setExp(0);
								p.setLevel(0);
								p.setTotalExperience(0);
								p.sendMessage(_("expfixothersreceive"));
							}
							else if (SetExpFix.getTotalExperience(p) >= 0)
							{
								user.sendMessage(_("expfixothersfalse", p.getDisplayName()));
							}
						}
					}
				}
			}
		}
	}	
}
