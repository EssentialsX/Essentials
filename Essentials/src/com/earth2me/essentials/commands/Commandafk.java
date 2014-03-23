package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import org.bukkit.Server;


public class Commandafk extends EssentialsCommand
{
	public Commandafk()
	{
		super("afk");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length > 0 && user.isAuthorized("essentials.afk.others"))
		{
			User afkUser = getPlayer(server, user, args, 0);
			toggleAfk(afkUser);
		}
		else
		{
			toggleAfk(user);
		}
	}
	
	@Override
	public void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length > 0)
		{
			User afkUser = getPlayer(server, args, 0, true, false);
			toggleAfk(afkUser);
		}
		else
		{
			throw new NotEnoughArgumentsException();
		}
	}

	private void toggleAfk(User user)
	{
		user.setDisplayNick();
		String msg = "";
		if (!user.toggleAfk())
		{
			//user.sendMessage(_("markedAsNotAway"));
			if (!user.isHidden())
			{
				msg = tl("userIsNotAway", user.getDisplayName());
			}
			user.updateActivity(false);
		}
		else
		{
			//user.sendMessage(_("markedAsAway"));
			if (!user.isHidden())
			{
				msg = tl("userIsAway", user.getDisplayName());
			}
		}
		if (!msg.isEmpty())
		{
			ess.broadcastMessage(user, msg);
		}
	}
}

