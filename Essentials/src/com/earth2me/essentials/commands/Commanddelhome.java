package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import java.util.Locale;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commanddelhome extends EssentialsCommand
{
	public Commanddelhome()
	{
		super("delhome");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		User user = ess.getUser(sender);
		String name;
		String[] expandedArg;

		//Allowing both formats /sethome khobbits house | /sethome khobbits:house
		final String[] nameParts = args[0].split(":");
		if (nameParts[0].length() != args[0].length())
		{
			expandedArg = nameParts;
		}
		else
		{
			expandedArg = args;
		}

		if (expandedArg.length > 1 && (user == null || user.isAuthorized("essentials.delhome.others")))
		{
			user = getPlayer(server, expandedArg, 0, true);
			name = expandedArg[1];
		}
		else if (user == null)
		{
			throw new NotEnoughArgumentsException();
		}
		else
		{
			name = expandedArg[0];
		}
		
		
		if (name.equalsIgnoreCase("bed")) { throw new Exception(_("invalidHomeName")); }
		
		user.delHome(name.toLowerCase(Locale.ENGLISH));
		sender.sendMessage(_("deleteHome", name));
	}
}
