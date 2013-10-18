package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;


public class Commandfly extends EssentialsToggleCommand
{
	public Commandfly()
	{
		super("fly", "essentials.fly.others");
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
			enabled = !user.getAllowFlight();
		}

		user.setFallDistance(0f);
		user.setAllowFlight(enabled);
		
		if (!user.getAllowFlight())
		{
			user.setFlying(false);
		}

		user.sendMessage(_("flyMode", _(enabled ? "enabled" : "disabled"), user.getDisplayName()));
		if (!sender.isPlayer() || !sender.getPlayer().equals(user.getBase()))
		{
			sender.sendMessage(_("flyMode", _(enabled ? "enabled" : "disabled"), user.getDisplayName()));
		}
	}
}
