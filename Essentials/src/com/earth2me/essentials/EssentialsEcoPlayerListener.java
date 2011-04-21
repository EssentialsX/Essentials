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

@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{

		if (Essentials.getSettings().areSignsDisabled()) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		User user = User.get(event.getPlayer());
		if (event.getClickedBlock().getType() != Material.WALL_SIGN && event.getClickedBlock().getType() != Material.SIGN_POST)
			return;
		Sign sign = new CraftSign(event.getClickedBlock());

		if (sign.getLine(0).equals("§1[Buy]") && user.isAuthorized("essentials.signs.buy.use"))
		{
			try
			{
				int amount = Integer.parseInt(sign.getLine(1));
				ItemStack item = ItemDb.get(sign.getLine(2), amount);
				int cost = Integer.parseInt(sign.getLine(3).substring(1));
				if (user.getMoney() < cost) throw new Exception("You do not have sufficient funds.");
				user.takeMoney(cost);
				Map<Integer, ItemStack> leftOver = user.getInventory().addItem(item);
				for (ItemStack itemStack : leftOver.values()) {
					user.getWorld().dropItem(user.getLocation(), itemStack);
				}
				user.updateInventory();
			}
			catch (Throwable ex)
			{
				user.sendMessage("§cError: " + ex.getMessage());
			}
			return;
		}

		if (sign.getLine(0).equals("§1[Sell]") && user.isAuthorized("essentials.signs.sell.use"))
		{
			try
			{
				int amount = Integer.parseInt(sign.getLine(1));
				ItemStack item = ItemDb.get(sign.getLine(2), amount);
				int cost = Integer.parseInt(sign.getLine(3).substring(1));
				if (!InventoryWorkaround.containsItem(user.getInventory(), true, item)) throw new Exception("You do not have enough items to sell.");
				user.giveMoney(cost);
				InventoryWorkaround.removeItem(user.getInventory(), true, item);
				user.updateInventory();
			}
			catch (Throwable ex)
			{
				user.sendMessage("§cError: " + ex.getMessage());
			}
			return;
		}

		if (sign.getLine(0).equals("§1[Trade]") && user.isAuthorized("essentials.signs.trade.use"))
		{
			try
			{
				String[] l1 = sign.getLines()[1].split("[ :-]+");
				String[] l2 = sign.getLines()[2].split("[ :-]+");
				boolean m1 = l1[0].matches("\\$[0-9]+");
				boolean m2 = l2[0].matches("\\$[0-9]+");
				int q1 = Integer.parseInt(m1 ? l1[0].substring(1) : l1[0]);
				int q2 = Integer.parseInt(m2 ? l2[0].substring(1) : l2[0]);
				int r1 = Integer.parseInt(l1[m1 ? 1 : 2]);
				int r2 = Integer.parseInt(l2[m2 ? 1 : 2]);
				r1 = r1 - r1 % q1;
				r2 = r2 - r2 % q2;
				if (q1 < 1 || q2 < 1) throw new Exception("Quantities must be greater than 0.");

				ItemStack i1 = m1 || r1 <= 0? null : ItemDb.get(l1[1], r1);
				ItemStack qi1 = m1 ? null : ItemDb.get(l1[1], q1);
				ItemStack qi2 = m2 ? null : ItemDb.get(l2[1], q2);

				if (user.getName().substring(0, 14).equals(sign.getLines()[3].substring(2)))
				{
					if (m1)
					{
						user.giveMoney(r1);
					}
					else if (i1 != null)
					{
						user.getInventory().addItem(i1);
						user.updateInventory();
					}
					r1 = 0;
					sign.setLine(1, (m1 ? "$" + q1 : q1 + " " + l1[1]) + ":" + r1);
				}
				else
				{
					if (m1)
					{
						if (user.getMoney() < q1)
							throw new Exception("You do not have sufficient funds.");
					}
					else
					{
						if (!InventoryWorkaround.containsItem(user.getInventory(), true, qi1))
							throw new Exception("You do not have " + q1 + "x " + l1[1] + ".");
					}

					if (r2 < q2) throw new Exception("The trade sign does not have enough supply left.");

					if (m1)
						user.takeMoney(q1);
					else
						InventoryWorkaround.removeItem(user.getInventory(), true, qi1);

					if (m2)
						user.giveMoney(q2);
					else
						user.getInventory().addItem(qi2);

					user.updateInventory();

					r1 += q1;
					r2 -= q2;

					sign.setLine(0, "§1[Trade]");
					sign.setLine(1, (m1 ? "$" + q1 : q1 + " " + l1[1]) + ":" + r1);
					sign.setLine(2, (m2 ? "$" + q2 : q2 + " " + l2[1]) + ":" + r2);

					user.sendMessage("§7Trade completed.");
				}
			}
			catch (Throwable ex)
			{
				user.sendMessage("§cError: " + ex.getMessage());
			}
			return;
		}
	}
}
