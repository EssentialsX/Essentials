package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n._;
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
			user.sendMessage(kitList.length() > 0 ? _("kits", kitList) : _("noKits"));
			throw new NoChargeException();
		}
		else if (args.length > 1 && user.isAuthorized("essentials.kit.others"))
		{
			final User userTo = getPlayer(server, user, args, 1);
			final String kitName = StringUtil.sanitizeString(args[0].toLowerCase(Locale.ENGLISH)).trim();
			giveKit(userTo, user, kitName);
		}
		else
		{
			final String kitName = StringUtil.sanitizeString(args[0].toLowerCase(Locale.ENGLISH)).trim();
			giveKit(user, user, kitName);
		}
	}

	@Override
	public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			final String kitList = Kit.listKits(ess, null);
			sender.sendMessage(kitList.length() > 0 ? _("kits", kitList) : _("noKits"));
			throw new NoChargeException();
		}
		else
		{
			final User userTo = getPlayer(server, args, 1, true, false);
			final String kitName = args[0].toLowerCase(Locale.ENGLISH);

			final Map<String, Object> kit = ess.getSettings().getKit(kitName);
			final List<String> items = Kit.getItems(ess, userTo, kitName, kit);
			Kit.expandItems(ess, userTo, items);

			sender.sendMessage(_("kitGiveTo", kitName, userTo.getDisplayName()));
			userTo.sendMessage(_("kitReceive", kitName));
		}
	}

	private void giveKit(User userTo, User userFrom, String kitName) throws Exception
	{		
		if (kitName.isEmpty())
		{
			throw new Exception(_("kitError2"));
		}
		
		final Map<String, Object> kit = ess.getSettings().getKit(kitName);

		if (!userFrom.isAuthorized("essentials.kits." + kitName))
		{
			throw new Exception(_("noKitPermission", "essentials.kits." + kitName));
		}

		final List<String> items = Kit.getItems(ess, userTo, kitName, kit);

		final Trade charge = new Trade("kit-" + kitName, ess);
		charge.isAffordableFor(userFrom);

		Kit.checkTime(userFrom, kitName, kit);
		Kit.expandItems(ess, userTo, items);

		charge.charge(userFrom);
		userFrom.sendMessage(_("kitGiveTo", kitName, userTo.getDisplayName()));
		userTo.sendMessage(_("kitReceive", kitName));
	}
}
