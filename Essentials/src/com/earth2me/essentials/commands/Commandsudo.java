package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;


public class Commandsudo extends EssentialsCommand
{
	public Commandsudo()
	{
		super("sudo");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		final User user = getPlayer(server, args, 0, true);
		final String command = args[1];
		String[] arguments = new String[args.length - 2];
		System.arraycopy(args, 2, arguments, 0, args.length - 2);

		//TODO: Translate this.
		sender.sendMessage("Running the command as " + user.getDisplayName());

		final PluginCommand pc = ess.getServer().getPluginCommand(command);
		if (pc != null)
		{
			pc.execute(user, command, arguments);
		}

	}
}
