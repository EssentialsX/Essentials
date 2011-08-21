package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
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
			user.sendMessage(Util.i18n("powerToolAir"));
			return;
		}

		String itemName = is.getType().toString().toLowerCase().replaceAll("_", " ");
		String command = getFinalArg(args, 0);
		if (command != null && !command.isEmpty())
		{
			if (command.equalsIgnoreCase("list"))
			{
				String powertools = user.getPowertool(is);
				if (powertools == null)
				{
					user.sendMessage(Util.format("powerToolListEmpty", itemName));
				}
				else
				{
					user.sendMessage(Util.format("powerToolList", powertools.replace("|", ", "), itemName));
				}
				return;
			}
			if (command.startsWith("r:"))
			{
				try
				{
					String removedCommand = command.substring(2);
					command = removePowerTool(user, removedCommand, is, itemName);
					user.sendMessage(Util.format("powerToolRemove", removedCommand, itemName));
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
					try
					{
						command = appendPowerTool(user, command, is, itemName);
					}
					catch (Exception e)
					{
						user.sendMessage(e.getMessage());
						return;
					}
				}

				user.sendMessage(Util.format("powerToolAttach", command.replace("|", ", "), itemName));
			}
		}
		else
		{
			user.sendMessage(Util.format("powerToolRemoveAll", itemName));
		}

		charge(user);
		user.setPowertool(is, command);
	}

	private String appendPowerTool(User user, String command, ItemStack is, String itemName) throws Exception
	{
		command = command.substring(2); // Ignore the first 2 chars
		String powertools = user.getPowertool(is);
		if (powertools != null)
		{
			if (powertools.contains(command))
			{
				throw new Exception((Util.format("powerToolAlreadySet", command, itemName)));
			}

			StringBuilder newCommand = new StringBuilder();
			command = newCommand.append(powertools).append("|").append(command).toString();
		}

		return command;
	}

	private String removePowerTool(User user, String command, ItemStack is, String itemName) throws Exception
	{
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

		return command;
	}
}
