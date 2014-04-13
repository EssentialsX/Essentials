package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.SetExpFix;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.NumberUtil;
import java.util.Locale;
import org.bukkit.Server;


public class Commandwhois extends EssentialsCommand
{
	public Commandwhois()
	{
		super("whois");
	}

	@Override
	public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		User user = getPlayer(server, sender, args, 0);

		sender.sendMessage(tl("whoisTop", user.getName()));
		user.setDisplayNick();
		sender.sendMessage(tl("whoisNick", user.getDisplayName()));
		sender.sendMessage(tl("whoisHealth", user.getBase().getHealth()));
		sender.sendMessage(tl("whoisHunger", user.getBase().getFoodLevel(), user.getBase().getSaturation()));
		sender.sendMessage(tl("whoisExp", SetExpFix.getTotalExperience(user.getBase()), user.getBase().getLevel()));
		sender.sendMessage(tl("whoisLocation", user.getLocation().getWorld().getName(), user.getLocation().getBlockX(), user.getLocation().getBlockY(), user.getLocation().getBlockZ()));
		if (!ess.getSettings().isEcoDisabled())
		{
			sender.sendMessage(tl("whoisMoney", NumberUtil.displayCurrency(user.getMoney(), ess)));
		}
		sender.sendMessage(tl("whoisIPAddress", user.getBase().getAddress().getAddress().toString()));
		final String location = user.getGeoLocation();
		if (location != null
			&& (sender.isPlayer() ? ess.getUser(sender.getPlayer()).isAuthorized("essentials.geoip.show") : true))
		{
			sender.sendMessage(tl("whoisGeoLocation", location));
		}
		sender.sendMessage(tl("whoisGamemode", tl(user.getBase().getGameMode().toString().toLowerCase(Locale.ENGLISH))));
		sender.sendMessage(tl("whoisGod", (user.isGodModeEnabled() ? tl("true") : tl("false"))));
		sender.sendMessage(tl("whoisOp", (user.getBase().isOp() ? tl("true") : tl("false"))));
		sender.sendMessage(tl("whoisFly", user.getBase().getAllowFlight() ? tl("true") : tl("false"), user.getBase().isFlying() ? tl("flying") : tl("notFlying")));
		sender.sendMessage(tl("whoisAFK", (user.isAfk() ? tl("true") : tl("false"))));
		sender.sendMessage(tl("whoisJail", (user.isJailed()
										   ? user.getJailTimeout() > 0
											 ? DateUtil.formatDateDiff(user.getJailTimeout())
											 : tl("true")
										   : tl("false"))));
		sender.sendMessage(tl("whoisMuted", (user.isMuted()
											? user.getMuteTimeout() > 0
											  ? DateUtil.formatDateDiff(user.getMuteTimeout())
											  : tl("true")
											: tl("false"))));

	}
}
