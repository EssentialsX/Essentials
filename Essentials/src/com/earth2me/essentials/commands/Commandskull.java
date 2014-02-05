package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.LocationUtil;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Commandskull extends EssentialsCommand
{
	public Commandskull()
	{
		super("skull");
	}
	
	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.getInventory().addItem(spawnSkull(user.getName(), 1));
			user.sendMessage(_("givenSkull", user.getName()));
		} else {
			// Alphanumeric and underscores only
			if (!args[0].matches("^[A-Za-z0-9_]+$")) {
				throw new IllegalArgumentException(_("invalidSkullOwner"));
			}

			final Location target = LocationUtil.getTarget(user.getBase());
			BlockState state = target.getBlock().getState();
			Skull skull = null;

			if (state instanceof Skull)
			{
				skull = (Skull) state;
				if (skull.hasOwner() && !user.isAuthorized("essentials.skull.change"))
				{
					throw new Exception(_("noPermissionSkull"));
				}
				
				user.sendMessage(_("skullChanged", args[0]));

				skull.setOwner(args[0]);
				skull.update(true);
				return;
			}

			if (skull == null) 
			{
				ItemStack cSkull = user.getItemInHand();
				if (cSkull.getType() == Material.SKULL_ITEM && cSkull.getDurability() == 3) {
					SkullMeta cSkullMeta = (SkullMeta) cSkull.getItemMeta();
					if (cSkullMeta.hasOwner() && !user.isAuthorized("essentials.skull.change"))
					{
						throw new Exception(_("noPermissionSkull"));
					}

					user.sendMessage(_("skullChanged", args[0]));
					user.setItemInHand(spawnSkull(args[0], cSkull.getAmount()));
				} else {
					throw new Exception(_("invalidSkull"));
				}
			}
		}
	}
	
	private ItemStack spawnSkull(String owner, int amount) {
		if (amount < 1 || amount > 64) {
			amount = 1;
		}
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, amount, (byte) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setDisplayName("§fSkull of " + owner);
		skullMeta.setOwner(owner);
		skull.setItemMeta(skullMeta);
		return skull;
	}
}