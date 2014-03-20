package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import org.bukkit.Server;


public class Commandtptoggle extends EssentialsToggleCommand
{
	public Commandtptoggle()
	{
		super("tptoggle", "essentials.tptoggle.others");
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
	void togglePlayer(CommandSource sender, User user, Boolean enabled)
	{
		if (enabled == null)
		{
			enabled = !user.isTeleportEnabled();
		}

		user.setTeleportEnabled(enabled);

		user.sendMessage(enabled ? tl("teleportationEnabled") : tl("teleportationDisabled"));
		if (!sender.isPlayer() || !sender.getPlayer().equals(user.getBase()))
		{
			sender.sendMessage(enabled ? tl("teleportationEnabledFor", user.getDisplayName()) : tl("teleportationDisabledFor", user.getDisplayName()));
		}
	}
}
