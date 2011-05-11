package com.earth2me.essentials.commands;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;



public class Commandsocialspy extends EssentialsCommand
{
	public Commandsocialspy()
	{
		super("socialspy");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		
		charge(user);
		user.sendMessage("§7SocialSpy " + (user.toggleSocialSpy() ? Util.i18n("enabled") : Util.i18n("disabled")));
	
	}
}
