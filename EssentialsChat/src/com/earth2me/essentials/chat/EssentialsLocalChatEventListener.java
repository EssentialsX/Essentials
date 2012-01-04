package com.earth2me.essentials.chat;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
 
public class EssentialsLocalChatEventListener extends CustomEventListener implements Listener {

	
	protected transient IEssentials ess;
	protected final transient Server server;

	public EssentialsLocalChatEventListener(final Server server, final IEssentials ess)
	{
		this.ess = ess;
		this.server = server;
	}
		
    public void onLocalChat(final EssentialsLocalChatEvent event) {		
		final Player sender = event.getPlayer();
		if (event.getRadius() < 1)
		{
			return;
		}
		event.setCancelled(true);
		final Location loc = sender.getLocation();
		final World world = loc.getWorld();

		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			String type = "[L]";
			final IUser user = ess.getUser(onlinePlayer);
			//TODO: remove reference to op 
			if (user.isIgnoringPlayer(sender.getName()) && !sender.isOp())
			{
				continue;
			}
			if (!user.equals(sender))
			{
				final Location playerLoc = user.getLocation();
				if (playerLoc.getWorld() != world)
				{
					continue;
				}
				final double delta = playerLoc.distanceSquared(loc);

				if (delta > event.getRadius())
				{
					if (user.isAuthorized("essentials.chat.spy"))
					{
						type = type.concat("[Spy]");
					}
					else
					{
						continue;
					}
				}
			}

			final String message = String.format(event.getFormat(), type.concat(sender.getDisplayName()), event.getMessage());
			user.sendMessage(message);
		}	
    }
 
    @Override
    public void onCustomEvent(final Event event) {
        if (event instanceof EssentialsLocalChatEvent) {
            onLocalChat((EssentialsLocalChatEvent) event);
        }
    }
}