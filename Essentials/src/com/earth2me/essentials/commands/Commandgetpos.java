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
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		Location coords = user.getLocation();
		user.sendMessage("§7X: " + coords.getBlockX() + " (+East <-> -West)");
		user.sendMessage("§7Y: " + coords.getBlockY() + " (+Up <-> -Down)");
		user.sendMessage("§7Z: " + coords.getBlockZ() + " (+South <-> -North)");
		user.sendMessage("§7Yaw: " + (coords.getYaw() + 180 + 360) % 360 + " (Rotation)");
		user.sendMessage("§7Pitch: " + coords.getPitch() + " (Head angle)");
	}
}
