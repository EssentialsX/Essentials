package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;


public class Commandsuicide extends EssentialsCommand
{
	public Commandsuicide()
	{
		super("suicide");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		user.setHealth(0);
		user.sendMessage(_("suicideMessage"));
		user.setDisplayNick();
		ess.broadcastMessage(user,_("suicideSuccess", user.getDisplayName()));		
	}
}
