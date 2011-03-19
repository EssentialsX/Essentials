package com.earth2me.essentials.spawn;

import java.util.logging.*;
import com.earth2me.essentials.*;
import com.earth2me.essentials.commands.IEssentialsCommand;
import org.bukkit.command.*;


public class EssentialsSpawnWorker
{
	private static final Logger logger = Logger.getLogger("Minecraft");

	@SuppressWarnings(
	{
		"LoggerStringConcat", "CallToThreadDumpStack"
	})
	public static boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		User user = User.get(sender);

		IEssentialsCommand cmd;
		try
		{
			cmd = (IEssentialsCommand)EssentialsSpawn.class.getClassLoader().loadClass("com.earth2me.essentials.spawn.Command" + command.getName()).newInstance();
		}
		catch (Exception ex)
		{
			sender.sendMessage("§cThat command is improperly loaded.");
			ex.printStackTrace();
			return true;
		}

		// Check authorization
		if (user != null && !user.isAuthorized(cmd))
		{
			logger.warning(user.getName() + " was denied access to command.");
			user.sendMessage("§cYou do not have access to that command.");
			return true;
		}

		// Run the command
		try
		{
			if (user == null)
				cmd.run(Essentials.getStatic().getServer(), Essentials.getStatic(), sender, commandLabel, command, args);
			else
				cmd.run(Essentials.getStatic().getServer(), Essentials.getStatic(), user, commandLabel, command, args);
			return true;
		}
		catch (Exception ex)
		{
			sender.sendMessage((user == null ? "" : "§c") + "Error: " + ex.getMessage());
			return true;
		}
	}
}
