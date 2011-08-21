package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.ArrayList;
import java.util.List;
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
		List<String> powertools = user.getPowertool(is);
		if (is == null || is.getType() == Material.AIR)
		{
			user.sendMessage(Util.i18n("powerToolAir"));
			return;
		}

		String itemName = is.getType().toString().toLowerCase().replaceAll("_", " ");
		String command = getFinalArg(args, 0);
		if (command != null && !command.isEmpty())
		{
			if (command.equalsIgnoreCase("list"))
			{
				if (powertools == null || powertools.isEmpty())
				{
					user.sendMessage(Util.format("powerToolListEmpty", itemName));
				}
				else
				{
					user.sendMessage(Util.format("powerToolList", powertools.toString(), itemName));
				}
				return;
			}
			if (command.startsWith("r:"))
			{
				try
				{
					command = command.substring(2);
					if (!powertools.contains(command))
					{
						user.sendMessage(Util.format("powerToolNoSuchCommandAssigned", command, itemName));
						return;
					}

					powertools.remove(command);
					user.sendMessage(Util.format("powerToolRemove", command, itemName));
				}
				catch (Exception e)
				{
					user.sendMessage(e.getMessage());
					return;
				}
			}
			else
			{
				if (command.startsWith("a:"))
				{
					command = command.substring(2);
					if(powertools.contains(command))
					{
						user.sendMessage(Util.format("powerToolAlreadySet", command, itemName));
						return;
					}
				}
				else if (powertools != null && !powertools.isEmpty())
				{
					// Replace all commands with this one
					powertools.clear();
				}
				else
				{
					powertools = new ArrayList<String>();
				}

				powertools.add(command);
				user.sendMessage(Util.format("powerToolAttach", powertools.toString(), itemName));
			}
		}
		else
		{
			user.sendMessage(Util.format("powerToolRemoveAll", itemName));
		}

		charge(user);
		user.setPowertool(is, powertools);
	}

	private String appendPowerTool(User user, String command, ItemStack is, String itemName) throws Exception
	{
		command = command.substring(2); // Ignore the first 2 chars
		/*String powertools = user.getPowertool(is);
		if (powertools != null)
		{
			if (powertools.contains(command))
			{
				throw new Exception((Util.format("powerToolAlreadySet", command, itemName)));
			}

			StringBuilder newCommand = new StringBuilder();
			command = newCommand.append(powertools).append("|").append(command).toString();
		}
*/
		return command;
	}

	private String removePowerTool(User user, String command, ItemStack is, String itemName) throws Exception
	{
		/*
		String powertools = user.getPowertool(is);
		if (!powertools.contains(command))
		{
			throw new Exception((Util.format("powerToolNoSuchCommandAssigned", command, itemName)));
		}

		command = powertools.replace(command, "").replace("||", "|");

		// Trim off any leading/trailing '|' chars
		if (command.startsWith("|"))
		{
			command = command.substring(1);
		}
		if (command.endsWith("|"))
		{
			command = command.substring(0, command.length() - 1);
		}
*/
		return command;
	}
}
