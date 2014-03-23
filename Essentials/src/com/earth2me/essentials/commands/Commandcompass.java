package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import org.bukkit.Server;


public class Commandcompass extends EssentialsCommand
{
	public Commandcompass()
	{
		super("compass");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		final int bearing = (int)(user.getLocation().getYaw() + 180 + 360) % 360;
		String dir;
		if (bearing < 23)
		{
			dir = "N";
		}
		else if (bearing < 68)
		{
			dir = "NE";
		}
		else if (bearing < 113)
		{
			dir = "E";
		}
		else if (bearing < 158)
		{
			dir = "SE";
		}
		else if (bearing < 203)
		{
			dir = "S";
		}
		else if (bearing < 248)
		{
			dir = "SW";
		}
		else if (bearing < 293)
		{
			dir = "W";
		}
		else if (bearing < 338)
		{
			dir = "NW";
		}
		else
		{
			dir = "N";
		}
		user.sendMessage(tl("compassBearing", dir, bearing));
	}
}
