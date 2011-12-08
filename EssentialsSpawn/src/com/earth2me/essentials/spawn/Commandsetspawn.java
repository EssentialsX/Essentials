package com.earth2me.essentials.spawn;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;
import org.bukkit.Server;


public class Commandsetspawn extends EssentialsCommand
{
	public Commandsetspawn()
	{
		super("setspawn");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		final String group = args.length > 0 ? getFinalArg(args, 0) : "default";
		((SpawnStorage)module).setSpawn(user.getLocation(), group);
		user.sendMessage(_("spawnSet", group));
	}
}
