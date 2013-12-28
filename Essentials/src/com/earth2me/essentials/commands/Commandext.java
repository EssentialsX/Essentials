package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.entity.Player;


public class Commandext extends EssentialsLoopCommand
{
	public Commandext()
	{
		super("ext");
	}

	@Override
	protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		loopOnlinePlayers(server, sender, true, true, args[0], null);
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			extPlayer(user.getBase());
			user.sendMessage(_("extinguish"));
			return;
		}

		loopOnlinePlayers(server, user.getSource(), true, true, args[0], null);
	}

	@Override
	protected void updatePlayer(final Server server, final CommandSource sender, final User player, final String[] args)
	{
		extPlayer(player.getBase());
		sender.sendMessage(_("extinguishOthers", player.getDisplayName()));
	}

	private void extPlayer(final Player player)
	{
		player.setFireTicks(0);
	}
}
