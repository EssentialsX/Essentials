package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.command.CommandSender;
import org.bukkit.Server;


public class Commandsocialspy extends EssentialsToggleCommand
{
	public Commandsocialspy()
	{
		super("socialspy", "essentials.socialspy.others");
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
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
				toggleOtherPlayers(server, user.getBase(), args);
			}
			else
			{
				togglePlayer(user.getBase(), user, toggle);
			}
		}
		else if (args.length == 2 && user.isAuthorized(othersPermission))
		{
			toggleOtherPlayers(server, user.getBase(), args);
		}
		else
		{
			togglePlayer(user.getBase(), user, null);
		}
	}

	@Override
	void togglePlayer(CommandSender sender, User user, Boolean enabled) throws NotEnoughArgumentsException
	{
		if (enabled == null)
		{
			enabled = !user.isSocialSpyEnabled();
		}

		user.setSocialSpyEnabled(enabled);


		user.sendMessage(_("socialSpy", user.getDisplayName(), enabled ? _("enabled") : _("disabled")));
		if (!sender.equals(user.getBase()))
		{
			sender.sendMessage(_("socialSpy", user.getDisplayName(), enabled ? _("enabled") : _("disabled")));
		}
	}
}
