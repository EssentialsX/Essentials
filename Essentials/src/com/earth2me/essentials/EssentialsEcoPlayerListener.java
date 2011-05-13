package com.earth2me.essentials;

import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;


public class EssentialsEcoPlayerListener extends PlayerListener
{
	Essentials ess;

	EssentialsEcoPlayerListener(Essentials ess)
	{
		this.ess = ess;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (ess.getSettings().areSignsDisabled())
		{
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
		{
			return;
		}
		User user = ess.getUser(event.getPlayer());
		String username = user.getName().substring(0, user.getName().length() > 14 ? 14 : user.getName().length());
		if (event.getClickedBlock().getType() != Material.WALL_SIGN && event.getClickedBlock().getType() != Material.SIGN_POST)
		{
			return;
		}
		Sign sign = new CraftSign(event.getClickedBlock());

		if (sign.getLine(0).equals("§1[Buy]") && user.isAuthorized("essentials.signs.buy.use"))
		{
			try
			{
				int amount = Integer.parseInt(sign.getLine(1));
				ItemStack item = ItemDb.get(sign.getLine(2), amount);
				double cost = Double.parseDouble(sign.getLine(3).substring(1));
				if (user.getMoney() < cost)
				{
					throw new Exception(Util.i18n("notEnoughMoney"));
				}
				user.takeMoney(cost);
				Map<Integer, ItemStack> leftOver = user.getInventory().addItem(item);
				for (ItemStack itemStack : leftOver.values())
				{
					user.getWorld().dropItem(user.getLocation(), itemStack);
				}
				user.updateInventory();
			}
			catch (Throwable ex)
			{
				user.sendMessage(Util.format("errorWithMessage", ex.getMessage()));
			}
			return;
		}

		if (sign.getLine(0).equals("§1[Sell]") && user.isAuthorized("essentials.signs.sell.use"))
		{
			try
			{
				int amount = Integer.parseInt(sign.getLine(1));
				ItemStack item = ItemDb.get(sign.getLine(2), amount);
				double cost = Double.parseDouble(sign.getLine(3).substring(1));
				
				if (!InventoryWorkaround.containsItem(user.getInventory(), true, item))
				{
					throw new Exception(Util.format("missingItems", amount, sign.getLine(2)));
				}
				user.giveMoney(cost);
				InventoryWorkaround.removeItem(user.getInventory(), true, item);
				user.updateInventory();
			}
			catch (Throwable ex)
			{
				user.sendMessage(Util.format("errorWithMessage", ex.getMessage()));
			}
			return;
		}

		if (sign.getLine(0).equals("§1[Trade]") && user.isAuthorized("essentials.signs.trade.use"))
		{
			try
			{
				String[] l1 = sign.getLines()[1].split("[ :-]+");
				String[] l2 = sign.getLines()[2].split("[ :-]+");
				boolean m1 = l1[0].matches("[^0-9][0-9]+(\\.[0-9]+)?");
				boolean m2 = l2[0].matches("[^0-9][0-9]+(\\.[0-9]+)?");
				double q1 = Double.parseDouble(m1 ? l1[0].substring(1) : l1[0]);
				double q2 = Double.parseDouble(m2 ? l2[0].substring(1) : l2[0]);
				double r1 = Double.parseDouble(l1[m1 ? 1 : 2]);
				double r2 = Double.parseDouble(l2[m2 ? 1 : 2]);
				r1 = m1 ? r1 : r1 - r1 % q1;
				r2 = m2 ? r2 : r2 - r2 % q2;
				if ((!m1 & q1 < 1) || (!m2 & q2 < 1))
				{
					throw new Exception(Util.i18n("moreThanZero"));
				}

				ItemStack i1 = m1 || r1 <= 0 ? null : ItemDb.get(l1[1], (int)r1);
				ItemStack qi1 = m1 ? null : ItemDb.get(l1[1], (int)q1);
				ItemStack qi2 = m2 ? null : ItemDb.get(l2[1], (int)q2);

				if (username.equals(sign.getLines()[3].substring(2)))
				{
					if (m1)
					{
						user.giveMoney(r1);
					}
					else if (i1 != null)
					{
						Map<Integer, ItemStack> leftOver = user.getInventory().addItem(i1);
						for (ItemStack itemStack : leftOver.values())
						{
							user.getWorld().dropItem(user.getLocation(), itemStack);
						}
						user.updateInventory();
					}
					r1 = 0;
					sign.setLine(1, (m1 ? Util.formatCurrency(q1) : ((int)q1) + " " + l1[1]) + ":" + r1);
					sign.update();
				}
				else
				{
					if (m1)
					{
						if (user.getMoney() < q1)
						{
							throw new Exception(Util.i18n("notEnoughMoney"));
						}
					}
					else
					{
						if (!InventoryWorkaround.containsItem(user.getInventory(), true, qi1))
						{
							throw new Exception(Util.format("missingItems", (int)q1, l1[1]));
						}
					}

					if (r2 < q2)
					{
						throw new Exception(Util.i18n("tradeSignEmpty"));
					}

					if (m1)
					{
						user.takeMoney(q1);
					}
					else
					{
						InventoryWorkaround.removeItem(user.getInventory(), true, qi1);
					}

					if (m2)
					{
						user.giveMoney(q2);
					}
					else
					{
						Map<Integer, ItemStack> leftOver = user.getInventory().addItem(qi2);
						for (ItemStack itemStack : leftOver.values())
						{
							user.getWorld().dropItem(user.getLocation(), itemStack);
						}
					}

					user.updateInventory();

					r1 += q1;
					r2 -= q2;
					

					sign.setLine(0, "§1[Trade]");
					sign.setLine(1, (m1 ? Util.formatCurrency(q1) : ((int)q1) + " " + l1[1]) + ":" + String.format((m1 ? "%.2f" : "%.0f"), Util.roundDouble(r1)));
					sign.setLine(2, (m2 ? Util.formatCurrency(q2) : ((int)q2) + " " + l2[1]) + ":" + String.format((m2 ? "%.2f" : "%.0f"), Util.roundDouble(r2)));
					sign.update();
					user.sendMessage(Util.i18n("tradeCompleted"));
				}
			}
			catch (Throwable ex)
			{
				user.sendMessage(Util.format("errorWithMessage", ex.getMessage()));
			}
			return;
		}
	}
}
