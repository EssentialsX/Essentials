package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;


public class Commandlightning extends EssentialsLoopCommand
{
	int power = 5;

	public Commandlightning()
	{
		super("lightning");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		User user = null;
		if (sender instanceof Player)
		{
			user = ess.getUser(((Player)sender));
			if ((args.length < 1 || user != null && !user.isAuthorized("essentials.lightning.others")))
			{
				user.getWorld().strikeLightning(user.getTargetBlock(null, 600).getLocation());
				return;
			}
		}

		if (args.length > 1)
		{
			try
			{
				power = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException ex)
			{
			}
		}
		loopOnlinePlayers(server, sender, true, args[0], null);
	}

	@Override
	protected void updatePlayer(final Server server, final CommandSender sender, final User matchUser, final String[] args)
	{
		sender.sendMessage(_("lightningUse", matchUser.getDisplayName()));
		final LightningStrike strike = matchUser.getBase().getWorld().strikeLightningEffect(matchUser.getBase().getLocation());

		if (!matchUser.isGodModeEnabled())
		{
			matchUser.getBase().damage(power, strike);
		}
		if (ess.getSettings().warnOnSmite())
		{
			matchUser.sendMessage(_("lightningSmited"));
		}
	}
}
