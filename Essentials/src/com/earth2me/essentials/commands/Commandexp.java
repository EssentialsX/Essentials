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
				if (user.isAuthorized("essentials.exp.needed"))
				{
					int totalexp = SetExpFix.getTotalExperience(user);
					int expleft = (int)Util.roundDouble(((((3.5 * user.getLevel()) + 6.7) - (totalexp - ((1.75 *(user.getLevel() * user.getLevel())) + (5.00 * user.getLevel())))) + 1));
					sender.sendMessage(_("expneeded", SetExpFix.getTotalExperience(user),"" + expleft));
				}
				else if (user.isAuthorized("essentials.exp"))
				{
					sender.sendMessage(_("exp", SetExpFix.getTotalExperience(user)));
				}
			}
		}	
	}
}
