package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;


public class Commandsocialspy extends EssentialsCommand
{
	public Commandsocialspy()
	{
		super("socialspy");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		user.sendMessage("§7SocialSpy " + (user.toggleSocialSpy() ? _("enabled") : _("disabled")));
	}
}
