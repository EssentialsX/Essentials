package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;


public class Commandpowertool extends EssentialsCommand
{
	public Commandpowertool()
	{
		super("powertool");
	}

	@Override
	protected void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{

		ItemStack is = user.getItemInHand();
		if (is == null || is.getType() == Material.AIR)
		{
			user.sendMessage("Command can't be attached to air.");
		}
		String command = getFinalArg(args, 0);
		if (command != null && !command.isEmpty())
		{
			user.sendMessage("Command assigned to " + is.getType().toString().toLowerCase().replaceAll("_", " "));
		}
		else
		{
			user.sendMessage("Command removed from " + is.getType().toString().toLowerCase().replaceAll("_", " "));
		}
		charge(user);
		user.setPowertool(is, command);
	}
}
