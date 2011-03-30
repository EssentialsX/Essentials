package com.earth2me.essentials.commands;

import org.bukkit.Location;
import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;


public class Commandgetpos extends EssentialsCommand
{
	public Commandgetpos()
	{
		super("getpos");
	}

	@Override
	public String[] getTriggers()
	{
		return new String[] { getName(), "coords" };
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		user.charge(this);
		Location coords = user.getLocation();
		user.sendMessage("§7X: " + coords.getBlockX() + " (-North <-> +South)");
		user.sendMessage("§7Y: " + coords.getBlockY() + " (+Up <-> -Down)");
		user.sendMessage("§7Z: " + coords.getBlockZ() + " (+East <-> -West)");
		user.sendMessage("§7Yaw: " + user.getCorrectedYaw() + " (Rotation)");
		user.sendMessage("§7Pitch: " + coords.getPitch() + " (Head angle)");
	}
}
