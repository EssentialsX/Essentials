package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;


public class Commandtptoggle extends EssentialsCommand
{
	public Commandtptoggle()
	{
		super("tptoggle");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		User otherUser = null;
		if (args.length > 0 && user.isAuthorized("essentials.tptoggle.others"))
		{
			otherUser = ess.getUser(server.getPlayer(args[0]));
				if (otherUser == null)
				{
					throw new Exception(_("playerNotFound"));
				}
				else
				{
				ess.getUser(server.getPlayer(args[0])).sendMessage(user.toggleTeleportEnabled()
						? _("teleportationEnabled")
						: _("teleportationDisabled"));
				}
		}
		else 
		{
		user.sendMessage(user.toggleTeleportEnabled()
						 ? _("teleportationEnabled")
						 : _("teleportationDisabled"));
		}
	}
}
