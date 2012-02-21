package com.earth2me.essentials.commands;

import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.perm.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;


public class Commandsudo extends EssentialsCommand
{
	@Override
	public void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		final IUser user = getPlayer(args, 0, false);
		final String command = args[1];
		final String[] arguments = new String[args.length - 2];
		if (arguments.length > 0)
		{
			System.arraycopy(args, 2, arguments, 0, args.length - 2);
		}

		//TODO: Translate this.
		if (Permissions.SUDO_EXEMPT.isAuthorized(user))
		{
			throw new Exception("You cannot sudo this user");
		}

		//TODO: Translate this.
		sender.sendMessage("Forcing " + user.getDisplayName() + " to run: /" + command + " " + arguments);

		final PluginCommand execCommand = ess.getServer().getPluginCommand(command);
		if (execCommand != null)
		{
			execCommand.execute(user.getBase(), command, arguments);
		}

	}
}
