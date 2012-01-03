package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import lombok.Cleanup;
import org.bukkit.command.CommandSender;


public class Commandunban extends EssentialsCommand
{
	@Override
	public void run(final CommandSender sender, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		try
		{
			@Cleanup
			final IUser player = getPlayer(args, 0, true);
			player.acquireWriteLock();
			player.getData().setBan(null);
			player.setBanned(false);
			sender.sendMessage(_("unbannedPlayer"));
		}
		catch (NoSuchFieldException e)
		{
			throw new Exception(_("playerNotFound"));
		}
	}
}
