package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Kit;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.api.IUser;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Commandkit extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String[] args) throws Exception
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
			final Object kit = ess.getSettings().getKit(kitName);

			if (!user.isAuthorized("essentials.kit." + kitName))
			{
				throw new Exception(_("noKitPermission", "essentials.kit." + kitName));
			}
			final Map<String, Object> els = (Map<String, Object>)kit;
			final List<String> items = Kit.getItems(user, els);

			Kit.checkTime(user, kitName, els);

			final Trade charge = new Trade("kit-" + kitName, ess);
			charge.isAffordableFor(user);

			Kit.expandItems(ess, user, items);

			charge.charge(user);
			user.sendMessage(_("kitGive", kitName));

		}
	}
}
