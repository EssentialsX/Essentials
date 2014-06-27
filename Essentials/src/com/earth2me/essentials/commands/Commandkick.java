package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import java.util.logging.Level;
import org.bukkit.Server;


public class Commandkick extends EssentialsCommand
{
	public Commandkick()
	{
		super("kick");
	}

	@Override
	public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		final User target = getPlayer(server, args, 0, true, false);
		if (sender.isPlayer())
		{
			User user = ess.getUser(sender.getPlayer());
			if (target.isHidden(sender.getPlayer()) && !user.canInteractVanished() && !sender.getPlayer().canSee(target.getBase()))
			{
				throw new PlayerNotFoundException();
			}

			if (target.isAuthorized("essentials.kick.exempt"))
			{
				throw new Exception(tl("kickExempt"));
			}
		}

		String kickReason = args.length > 1 ? getFinalArg(args, 1) : tl("kickDefault");
		kickReason = FormatUtil.replaceFormat(kickReason.replace("\\n", "\n").replace("|", "\n"));

		target.getBase().kickPlayer(kickReason);
		final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;

		server.getLogger().log(Level.INFO, tl("playerKicked", senderName, target.getName(), kickReason));
		ess.broadcastMessage("essentials.kick.notify", tl("playerKicked", senderName, target.getName(), kickReason));
	}
}
