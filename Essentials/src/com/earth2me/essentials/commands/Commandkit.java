package com.earth2me.essentials.commands;

import com.earth2me.essentials.*;
import static com.earth2me.essentials.I18n._;
import java.util.*;
import org.bukkit.Server;


public class Commandkit extends EssentialsCommand
{
	public Commandkit()
	{
		super("kit");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			final String kitList = Kit.listKits(ess, user);
			if (kitList.length() > 0)
			{
				user.sendMessage(_("kits", kitList));
			}
			else
			{
				user.sendMessage(_("noKits"));
			}
			throw new NoChargeException();
		}
		else
		{
			final String kitName = args[0].toLowerCase(Locale.ENGLISH);
			final Map<String, Object> kit = ess.getSettings().getKit(kitName);

			if (!user.isAuthorized("essentials.kit." + kitName))
			{
				throw new Exception(_("noKitPermission", "essentials.kit." + kitName));
			}

			final List<String> items = Kit.getItems(user, kit);

			Kit.checkTime(user, kitName, kit);

			final Trade charge = new Trade("kit-" + kitName, ess);
			charge.isAffordableFor(user);

			Kit.expandItems(ess, user, items);

			charge.charge(user);
			user.sendMessage(_("kitGive", kitName));

		}
	}
}
