package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.perm.Permissions;
import java.util.Locale;
import lombok.Cleanup;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commanddelhome extends EssentialsCommand
{
	@Override
	public void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		@Cleanup
		IUser user = sender instanceof Player ? ess.getUser((Player)sender) : null;
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

		if (expandedArg.length > 1 && (user == null || Permissions.DELHOME_OTHERS.isAuthorized(user)))
		{
			user = getPlayer(expandedArg, 0, true);
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
		//TODO: Think up a nice error message
		/*
		 * if (name.equalsIgnoreCase("bed")) { throw new Exception("You cannot remove the vanilla home point"); }
		 */
		user.acquireWriteLock();
		user.getData().removeHome(name.toLowerCase(Locale.ENGLISH));
		sender.sendMessage(_("deleteHome", name));
	}
}
