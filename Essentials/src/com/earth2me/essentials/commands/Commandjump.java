package com.earth2me.essentials.commands;

import org.bukkit.Location;
import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.TargetBlock;
import com.earth2me.essentials.User;


public class Commandjump extends EssentialsCommand
{
	public Commandjump()
	{
		super("jump");
	}

	@Override
	public String[] getTriggers()
	{
		return new String[]
				{
					getName(), "j"
				};
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		Location loc;
		Location cloc = user.getLocation();

		try
		{
			loc = new TargetBlock(user, 100, 2.65).getTargetBlock().getLocation();
			loc.setYaw(cloc.getYaw());
			loc.setPitch(cloc.getPitch());
			loc = new TargetBlock(loc).getPreviousBlock().getLocation();
			loc.setYaw(cloc.getYaw());
			loc.setPitch(cloc.getPitch());
			loc.setY(loc.getY() + 1);
		}
		catch (NullPointerException ex)
		{
			throw new Exception("That would hurt your computer's brain.", ex);
		}

		user.canAfford(this);
		user.teleportTo(loc, this.getName());
	}
}
