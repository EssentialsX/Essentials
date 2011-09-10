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
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		final ItemStack itemStack = user.getItemInHand();
		if (itemStack == null || itemStack.getType() == Material.AIR)
		{
			throw new Exception(Util.i18n("powerToolAir"));
		}

		final String itemName = itemStack.getType().toString().toLowerCase().replaceAll("_", " ");
		String command = getFinalArg(args, 0);
		List<String> powertools = user.getPowertool(itemStack);
		if (command != null && !command.isEmpty())
		{
			if (command.equalsIgnoreCase("l:"))
			{
				if (powertools == null || powertools.isEmpty())
				{
					throw new Exception(Util.format("powerToolListEmpty", itemName));
				}
				else
				{
					user.sendMessage(Util.format("powerToolList", Util.joinList(powertools), itemName));
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
						throw new Exception(Util.format("powerToolNoSuchCommandAssigned", command, itemName));
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
					if (powertools.contains(command))
					{
						throw new Exception(Util.format("powerToolAlreadySet", command, itemName));
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
				user.sendMessage(Util.format("powerToolAttach", Util.joinList(powertools), itemName));
			}
		}
		else
		{
			if (powertools != null)
			{
				powertools.clear();
			}
			user.sendMessage(Util.format("powerToolRemoveAll", itemName));
		}

		user.setPowertool(itemStack, powertools);
	}
}
