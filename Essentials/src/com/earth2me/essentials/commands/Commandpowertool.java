package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
		String command = getFinalArg(args, 0);

		// check to see if this is a clear all command
		if (command != null && command.equalsIgnoreCase("d:"))
		{
			user.clearAllPowertools();
			user.sendMessage(_("powerToolClearAll"));
			return;
		}

		final ItemStack itemStack = user.getItemInHand();
		if (itemStack == null || itemStack.getType() == Material.AIR)
		{
			throw new Exception(_("powerToolAir"));
		}

		final String itemName = itemStack.getType().toString().toLowerCase(Locale.ENGLISH).replaceAll("_", " ");
		List<String> powertools = user.getPowertool(itemStack);
		if (command != null && !command.isEmpty())
		{
			if (command.equalsIgnoreCase("l:"))
			{
				if (powertools == null || powertools.isEmpty())
				{
					throw new Exception(_("powerToolListEmpty", itemName));
				}
				else
				{
					user.sendMessage(_("powerToolList", Util.joinList(powertools), itemName));
				}
				throw new NoChargeException();
			}
			if (command.startsWith("r:"))
			{
				command = command.substring(2);
				if (!powertools.contains(command))
				{
					throw new Exception(_("powerToolNoSuchCommandAssigned", command, itemName));
				}

				powertools.remove(command);
				user.sendMessage(_("powerToolRemove", command, itemName));
			}
			else
			{
				if (command.startsWith("a:"))
				{
					if (!user.isAuthorized("essentials.powertool.append"))
					{
						throw new Exception(_("noPerm", "essentials.powertool.append"));
					}
					command = command.substring(2);
					if (powertools.contains(command))
					{
						throw new Exception(_("powerToolAlreadySet", command, itemName));
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
				user.sendMessage(_("powerToolAttach", Util.joinList(powertools), itemName));
			}
		}
		else
		{
			if (powertools != null)
			{
				powertools.clear();
			}
			user.sendMessage(_("powerToolRemoveAll", itemName));
		}

		if (!user.arePowerToolsEnabled())
		{
			user.setPowerToolsEnabled(true);
			user.sendMessage(_("powerToolsEnabled"));
		}
		user.setPowertool(itemStack, powertools);
	}
}
