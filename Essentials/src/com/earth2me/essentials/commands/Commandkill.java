package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;


public class Commandkill extends EssentialsLoopCommand
{
	public Commandkill()
	{
		super("kill");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		loopOnlinePlayers(server, sender, true, args[0], null);
	}

	@Override
	protected void updatePlayer(final Server server, final CommandSender sender, final User user, final String[] args) throws PlayerExemptException
	{
		final Player matchPlayer = user.getBase();
		if (sender instanceof Player && user.isAuthorized("essentials.kill.exempt") && !ess.getUser(sender).isAuthorized("essentials.kill.force"))
		{
			throw new PlayerExemptException(_("killExempt", matchPlayer.getDisplayName()));
		}
		final EntityDamageEvent ede = new EntityDamageEvent(matchPlayer, sender instanceof Player && ((Player)sender).getName().equals(matchPlayer.getName()) ? EntityDamageEvent.DamageCause.SUICIDE : EntityDamageEvent.DamageCause.CUSTOM, Short.MAX_VALUE);
		server.getPluginManager().callEvent(ede);
		if (ede.isCancelled() && sender instanceof Player && !ess.getUser(sender).isAuthorized("essentials.kill.force"))
		{
			return;
		}
		matchPlayer.damage(Short.MAX_VALUE);

		if (matchPlayer.getHealth() > 0)
		{
			matchPlayer.setHealth(0);
		}

		sender.sendMessage(_("kill", matchPlayer.getDisplayName()));
	}
}
