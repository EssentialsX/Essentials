package com.earth2me.essentials.commands;

import org.bukkit.Location;
import org.bukkit.Server;
import com.earth2me.essentials.User;


public class Commandgetpos extends EssentialsCommand
{
	public Commandgetpos()
	{
		super("getpos");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		final Location coords = user.getLocation();
		user.sendMessage("§7X: " + coords.getBlockX() + " (+East <-> -West)");
		user.sendMessage("§7Y: " + coords.getBlockY() + " (+Up <-> -Down)");
		user.sendMessage("§7Z: " + coords.getBlockZ() + " (+South <-> -North)");
		user.sendMessage("§7Yaw: " + (coords.getYaw() + 180 + 360) % 360 + " (Rotation)");
		user.sendMessage("§7Pitch: " + coords.getPitch() + " (Head angle)");
	}
}
