package com.earth2me.essentials.spawn;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Commandspawn extends EssentialsCommand
{
	public Commandspawn()
	{
		super("spawn");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{	
		final Trade charge = new Trade(this.getName(), ess);
		charge.isAffordableFor(user);
		if (args.length > 0 && user.isAuthorized("essentials.spawn.others"))
		{
			final User otherUser = getPlayer(server, args, 0);
			respawn(otherUser, null);
			if (!otherUser.equals(user))
			{
				otherUser.sendMessage(_("teleportAtoB", user.getDisplayName(), "spawn"));
				user.sendMessage(_("teleporting"));
			}
		}
		else
		{	
			respawn(user, null);
		}
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		final User user = getPlayer(server, args, 0);
		respawn(user, null);
		user.sendMessage(_("teleportAtoB", user.getDisplayName(), "spawn"));
		sender.sendMessage(_("teleporting"));
	}
	
	private void respawn (final User user, final Trade charge) throws Exception {
		final SpawnStorage spawns = (SpawnStorage)this.module;
		final Location spawn = spawns.getSpawn(user.getGroup());
		user.getTeleport().teleport(spawn, charge, TeleportCause.COMMAND);	
	}
}
