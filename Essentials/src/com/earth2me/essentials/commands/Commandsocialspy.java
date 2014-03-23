package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import org.bukkit.Server;


public class Commandsocialspy extends EssentialsToggleCommand
{
	public Commandsocialspy()
	{
		super("socialspy", "essentials.socialspy.others");
	}

	@Override
	protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		toggleOtherPlayers(server, sender, args);
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length == 1)
		{
			Boolean toggle = matchToggleArgument(args[0]);
			if (toggle == null && user.isAuthorized(othersPermission))
			{
				toggleOtherPlayers(server, user.getSource(), args);
			}
			else
			{
				togglePlayer(user.getSource(), user, toggle);
			}
		}
		else if (args.length == 2 && user.isAuthorized(othersPermission))
		{
			toggleOtherPlayers(server, user.getSource(), args);
		}
		else
		{
			togglePlayer(user.getSource(), user, null);
		}
	}

	@Override
	void togglePlayer(CommandSource sender, User user, Boolean enabled) throws NotEnoughArgumentsException
	{
		if (enabled == null)
		{
			enabled = !user.isSocialSpyEnabled();
		}

		user.setSocialSpyEnabled(enabled);


		user.sendMessage(tl("socialSpy", user.getDisplayName(), enabled ? tl("enabled") : tl("disabled")));
		if (!sender.isPlayer() || !sender.getPlayer().equals(user.getBase()))
		{
			sender.sendMessage(tl("socialSpy", user.getDisplayName(), enabled ? tl("enabled") : tl("disabled")));
		}
	}
}
