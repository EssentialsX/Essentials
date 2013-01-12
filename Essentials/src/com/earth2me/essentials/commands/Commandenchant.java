package com.earth2me.essentials.commands;

import com.earth2me.essentials.Enchantments;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.MetaItemStack;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;


public class Commandenchant extends EssentialsCommand
{
	public Commandenchant()
	{
		super("enchant");
	}

	//TODO: Implement charge costs: final Trade charge = new Trade("enchant-" + enchantmentName, ess);
	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		final MetaItemStack stack = new MetaItemStack(user.getItemInHand());
		if (stack == null)
		{
			throw new Exception(_("nothingInHand"));
		}
		if (args.length == 0)
		{
			final Set<String> enchantmentslist = new TreeSet<String>();
			for (Map.Entry<String, Enchantment> entry : Enchantments.entrySet())
			{
				final String enchantmentName = entry.getValue().getName().toLowerCase(Locale.ENGLISH);
				if (enchantmentslist.contains(enchantmentName) || (user.isAuthorized("essentials.enchant." + enchantmentName) && entry.getValue().canEnchantItem(stack)))
				{
					enchantmentslist.add(entry.getKey());
					//enchantmentslist.add(enchantmentName);
				}
			}
			throw new NotEnoughArgumentsException(_("enchantments", Util.joinList(enchantmentslist.toArray())));
		}

		int level = -1;
		if (args.length > 1)
		{
			try
			{
				level = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException ex)
			{
				level = -1;
			}
		}

		final boolean allowUnsafe = ess.getSettings().allowUnsafeEnchantments() && user.isAuthorized("essentials.enchant.allowunsafe");
		final Enchantment enchantment = stack.getEnchantment(user, args[0]);
		stack.addEnchantment(user, allowUnsafe, enchantment, level);


		user.getInventory().setItemInHand(stack.getBase());
		user.updateInventory();
		final String enchantmentName = enchantment.getName().toLowerCase(Locale.ENGLISH);
		if (level == 0)
		{
			user.sendMessage(_("enchantmentRemoved", enchantmentName.replace('_', ' ')));
		}
		else
		{
			user.sendMessage(_("enchantmentApplied", enchantmentName.replace('_', ' ')));
		}
	}
}
