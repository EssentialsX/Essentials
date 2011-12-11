package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import org.bukkit.Server;


public class Commandping extends EssentialsCommand
{
	public Commandping()
	{
		super("ping");
	}

	@Override
	public void run(final Server server, final IUser player, final String commandLabel, final String[] args) throws Exception
	{
		player.sendMessage(_("pong"));
	}
}
