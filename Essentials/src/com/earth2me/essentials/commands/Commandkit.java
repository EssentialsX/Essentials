package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.Kit;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.StringUtil;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
			user.sendMessage(kitList.length() > 0 ? tl("kits", kitList) : tl("noKits"));
			throw new NoChargeException();
		}
		else if (args.length > 1 && user.isAuthorized("essentials.kit.others"))
		{
			final User userTo = getPlayer(server, user, args, 1);
			final String kitNames = StringUtil.sanitizeString(args[0].toLowerCase(Locale.ENGLISH)).trim();
			giveKits(userTo, user, kitNames);
		}
		else
		{
			final String kitNames = StringUtil.sanitizeString(args[0].toLowerCase(Locale.ENGLISH)).trim();
			giveKits(user, user, kitNames);
		}
	}

	@Override
	public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			final String kitList = Kit.listKits(ess, null);
			sender.sendMessage(kitList.length() > 0 ? tl("kits", kitList) : tl("noKits"));
			throw new NoChargeException();
		}
		else
		{
			final User userTo = getPlayer(server, args, 1, true, false);
			final String[] kits = args[0].toLowerCase(Locale.ENGLISH).split(",");

			for (final String kitName : kits)
			{
				final Map<String, Object> kit = ess.getSettings().getKit(kitName);
				final List<String> items = Kit.getItems(ess, userTo, kitName, kit);
				Kit.expandItems(ess, userTo, items);

				sender.sendMessage(tl("kitGiveTo", kitName, userTo.getDisplayName()));
				userTo.sendMessage(tl("kitReceive", kitName));
			}
		}
	}

	private void giveKits(User userTo, User userFrom, String kitNames) throws Exception
	{
		if (kitNames.isEmpty())
		{
			throw new Exception(tl("kitError2"));
		}
		String[] kits = kitNames.split(",");

		for (final String kitName : kits)
		{
			giveKit(userTo, userFrom, kitName);
		}
	}

	private void giveKit(User userTo, User userFrom, String kitName) throws Exception
	{
		if (kitName.isEmpty())
		{
			throw new Exception(tl("kitError2"));
		}

		final Map<String, Object> kit = ess.getSettings().getKit(kitName);

		if (!userFrom.isAuthorized("essentials.kits." + kitName))
		{
			throw new Exception(tl("noKitPermission", "essentials.kits." + kitName));
		}

		final List<String> items = Kit.getItems(ess, userTo, kitName, kit);

		final Trade charge = new Trade("kit-" + kitName, new Trade("kit-kit", ess), ess);
		charge.isAffordableFor(userFrom);

		Kit.checkTime(userFrom, kitName, kit);
		Kit.expandItems(ess, userTo, items);

		charge.charge(userFrom);

		if (!userFrom.equals(userTo))
		{
			userFrom.sendMessage(tl("kitGiveTo", kitName, userTo.getDisplayName()));
		}

		userTo.sendMessage(tl("kitReceive", kitName));
	}
}
