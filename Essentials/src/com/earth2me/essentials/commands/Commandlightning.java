package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.entity.LightningStrike;


public class Commandlightning extends EssentialsLoopCommand
{
	int power = 5;

	public Commandlightning()
	{
		super("lightning");
	}

	@Override
	public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		User user;
		if (sender.isPlayer())
		{
			user = ess.getUser(sender.getPlayer());
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
		loopOnlinePlayers(server, sender, true, true, args[0], null);
	}

	@Override
	protected void updatePlayer(final Server server, final CommandSource sender, final User matchUser, final String[] args)
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
