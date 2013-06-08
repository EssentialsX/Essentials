package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.StringUtil;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandme extends EssentialsCommand
{
	public Commandme()
	{
		super("me");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (user.isMuted())
		{
			throw new Exception(_("voiceSilenced"));
		}

		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		String message = getFinalArg(args, 0);
		message = FormatUtil.formatMessage(user, "essentials.chat", message);	

		user.setDisplayNick();
		ess.broadcastMessage(user, _("action", user.getDisplayName(), message));
	}
	
	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{		
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		String message = getFinalArg(args, 0);
		message = FormatUtil.replaceFormat(message);	

		ess.getServer().broadcastMessage(_("action", "@", message));
	}
}
