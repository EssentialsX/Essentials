package com.earth2me.essentials.commands;

import com.earth2me.essentials.*;
import static com.earth2me.essentials.I18n._;
import java.util.*;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


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
			listKits(user);
			throw new NoChargeException();
		}
		else if (args.length > 1 && user.isAuthorized("essentials.kit.others"))
		{
			final User userTo = getPlayer(server, args, 1, true);
			final String kitName = args[0].toLowerCase(Locale.ENGLISH);
			giveKit(userTo, user, kitName);
		}
		else
		{
			final String kitName = args[0].toLowerCase(Locale.ENGLISH);
			giveKit(user, user, kitName);
		}
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			listKits(sender);
			throw new NoChargeException();
		}
		else
		{
			final User userTo = getPlayer(server, args, 1, true);
			final String kitName = args[0].toLowerCase(Locale.ENGLISH);

			final Map<String, Object> kit = ess.getSettings().getKit(kitName);
			final List<String> items = Kit.getItems(userTo, kit);
			Kit.expandItems(ess, userTo, items);

			sender.sendMessage(_("kitGive", kitName));
		}
	}

	private void listKits(CommandSender sender) throws Exception
	{
		final String kitList = Kit.listKits(ess, null);
		if (kitList.length() > 0)
		{
			sender.sendMessage(_("kits", kitList));
		}
		else
		{
			sender.sendMessage(_("noKits"));
		}
	}

	private void giveKit(User userTo, User userFrom, String kitName) throws Exception
	{
		final Map<String, Object> kit = ess.getSettings().getKit(kitName);

		if (!userFrom.isAuthorized("essentials.kit." + kitName))
		{
			throw new Exception(_("noKitPermission", "essentials.kit." + kitName));
		}

		final List<String> items = Kit.getItems(userTo, kit);

		Kit.checkTime(userFrom, kitName, kit);

		final Trade charge = new Trade("kit-" + kitName, ess);
		charge.isAffordableFor(userFrom);

		Kit.expandItems(ess, userTo, items);

		charge.charge(userFrom);
		userTo.sendMessage(_("kitGive", kitName));
	}
}
