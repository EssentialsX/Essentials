package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;


public class Commandping extends EssentialsCommand
{
	public Commandping()
	{
		super("ping");
	}

	@Override
	public void run(Server server, User player, String commandLabel, String[] args) throws Exception
	{
		player.sendMessage(_("pong"));
	}
}
