package com.earth2me.essentials;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;


public class EssentialsBlockListener extends BlockListener
{
	private final Essentials parent;
	public final static ArrayList<Material> protectedBlocks = new ArrayList<Material>(4);

	static
	{
		protectedBlocks.add(Material.CHEST);
		protectedBlocks.add(Material.BURNING_FURNACE);
		protectedBlocks.add(Material.FURNACE);
		protectedBlocks.add(Material.DISPENSER);
	}

	public EssentialsBlockListener(Essentials parent)
	{
		this.parent = parent;
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (event.isCancelled()) return;
		if (Essentials.getSettings().areSignsDisabled()) return;
		User user = User.get(event.getPlayer());
		if (protectedBlocks.contains(event.getBlock().getType()) && !user.isAuthorized("essentials.signs.protection.override"))
		{
			if (isBlockProtected(event.getBlock(), user))
			{
				event.setCancelled(true);
				user.sendMessage("§cYou do not have permission to destroy that chest.");
				return;
			}
		}

		if (checkProtectionSign(event.getBlock(), user) == NOT_ALLOWED)
		{
			event.setCancelled(true);
			user.sendMessage("§cYou do not have permission to destroy that sign.");
		}
	}

	@Override
	public void onSignChange(SignChangeEvent event)
	{
		if (event.isCancelled()) return;
		if (Essentials.getSettings().areSignsDisabled()) return;
		User user = User.get(event.getPlayer());

		try
		{
			if (event.getLine(0).equalsIgnoreCase("[Protection]"))
			{
				Block block = event.getBlock();
				if (user.isAuthorized("essentials.signs.protection.create") && hasAdjacentChest(block) && !isBlockProtected(block, user))
					event.setLine(0, "§1[Protection]");
				else
					event.setLine(0, "§4[Protection]");
				event.setLine(3, user.getName());
				return;
			}
			if (event.getLine(0).equalsIgnoreCase("[Disposal]"))
			{
				if (user.isAuthorized("essentials.signs.disposal.create"))
					event.setLine(0, "§1[Disposal]");
				else
					event.setLine(0, "§4[Disposal]");
				return;
			}
			if (event.getLine(0).equalsIgnoreCase("[Heal]"))
			{
				event.setLine(0, "§4[Heal]");
				if (user.isAuthorized("essentials.signs.heal.create"))
				{
					if (!event.getLine(1).isEmpty())
					{
						String[] l1 = event.getLine(1).split("[ :-]+");
						boolean m1 = l1[0].matches("\\$[0-9]+");
						int q1 = Integer.parseInt(m1 ? l1[0].substring(1) : l1[0]);
						if (q1 < 1) throw new Exception("Quantities must be greater than 0.");
						if (!m1) ItemDb.get(l1[1]);
						event.setLine(1, (m1 ? "$" + q1 : q1 + " " + l1[1]));
					}
					event.setLine(0, "§1[Heal]");
				}
				return;
			}
			if (event.getLine(0).equalsIgnoreCase("[Free]"))
			{
				event.setLine(0, "§4[Free]");
				ItemDb.get(event.getLine(1));
				if (user.isAuthorized("essentials.signs.free.create"))
					event.setLine(0, "§1[Free]");
				return;
			}
			if (event.getLine(0).equalsIgnoreCase("[Mail]"))
			{
				if (user.isAuthorized("essentials.signs.mail.create"))
					event.setLine(0, "§1[Mail]");
				else
					event.setLine(0, "§4[Mail]");
				return;
			}
			if (event.getLine(0).equalsIgnoreCase("[Balance]"))
			{
				if (user.isAuthorized("essentials.signs.balance.create"))
					event.setLine(0, "§1[Balance]");
				else
					event.setLine(0, "§4[Balance]");
				return;
			}
                        if (event.getLine(0).equalsIgnoreCase("[Warp]"))
			{
				event.setLine(0, "§4[Warp]");
				if (user.isAuthorized("essentials.signs.warp.create")) {
					if (!event.getLine(3).isEmpty())
					{
						String[] l1 = event.getLine(3).split("[ :-]+");
						boolean m1 = l1[0].matches("\\$[0-9]+");
						int q1 = Integer.parseInt(m1 ? l1[0].substring(1) : l1[0]);
						if (q1 < 1) throw new Exception("Quantities must be greater than 0.");
						if (!m1) ItemDb.get(l1[1]);
						event.setLine(3, (m1 ? "$" + q1 : q1 + " " + l1[1]));
					}
					if (event.getLine(1).isEmpty()) {
						event.setLine(1, "§dWarp name here!");
						return;
					} else {
						Essentials.getWarps().getWarp(event.getLine(1));
						if (event.getLine(2).equalsIgnoreCase("Everyone")) {
							event.setLine(2, "§2Everyone");
						}
						event.setLine(0, "§1[Warp]");						
					}
				}
				return;
			}
		}
		catch (Throwable ex)
		{
			user.sendMessage("§cError: " + ex.getMessage());
		}
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled()) return;
		Block signBlock = event.getBlockAgainst();
		if (signBlock.getType() == Material.WALL_SIGN || signBlock.getType() == Material.SIGN_POST)
		{
			Sign sign = new CraftSign(signBlock);
			if (sign.getLine(0).matches("§1\\[[a-zA-Z]+\\]"))
			{
				event.setCancelled(true);
				return;
			}
		}
		final User user = User.get(event.getPlayer());
		// Do not rely on getItemInHand();
		// http://leaky.bukkit.org/issues/663
		final ItemStack is = new ItemStack(event.getBlockPlaced().getType(), 1, (short)0, event.getBlockPlaced().getData());
		switch(is.getType()) {
			case WOODEN_DOOR:
				is.setType(Material.WOOD_DOOR);
				break;
			case IRON_DOOR_BLOCK:
				is.setType(Material.IRON_DOOR);
				break;
			case SIGN_POST:
			case WALL_SIGN:
				is.setType(Material.SIGN);
				break;
			case CROPS:
				is.setType(Material.SEEDS);
				break;
			case CAKE_BLOCK:
				is.setType(Material.CAKE);
				break;
			case BED_BLOCK:
				is.setType(Material.BED);
				break;
			case REDSTONE_WIRE:
				is.setType(Material.REDSTONE);
				break;
			case REDSTONE_TORCH_OFF:
				is.setType(Material.REDSTONE_TORCH_ON);
				break;
			case DIODE_BLOCK_OFF:
			case DIODE_BLOCK_ON:
				is.setType(Material.DIODE);
				break;
		}
		boolean unlimitedForUser = user.hasUnlimited(is);
		if (unlimitedForUser) {
			Essentials.getStatic().getScheduler().scheduleSyncDelayedTask(Essentials.getStatic(), 
				new Runnable() {

				public void run() {
					user.getInventory().addItem(is);
					user.updateInventory();
				}
			});
		}
	}

	public boolean hasAdjacentChest(Block block)
	{
		Block[] faces = getAdjacentBlocks(block);
		for (Block b : faces)
		{
			if (protectedBlocks.contains(b.getType()))
			{
				return true;
			}
		}
		return false;
	}
	private static final int NOT_ALLOWED = 0;
	private static final int ALLOWED = 1;
	private static final int NOSIGN = 2;

	private int checkProtectionSign(Block block, User user)
	{
		if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)
		{
			Sign sign = new CraftSign(block);
			if (sign.getLine(0).equalsIgnoreCase("§1[Protection]") && !user.isAuthorized("essentials.signs.protection.override"))
			{
				if (sign.getLine(1).equalsIgnoreCase(user.getName()))
				{
					return ALLOWED;
				}
				if (sign.getLine(2).equalsIgnoreCase(user.getName()))
				{
					return ALLOWED;
				}
				if (sign.getLine(3).equalsIgnoreCase(user.getName()))
				{
					return ALLOWED;
				}
				return NOT_ALLOWED;
			}
		}
		return NOSIGN;
	}

	private static Block[] getAdjacentBlocks(Block block)
	{
		return new Block[]
				{
					block.getFace(BlockFace.NORTH),
					block.getFace(BlockFace.SOUTH),
					block.getFace(BlockFace.EAST),
					block.getFace(BlockFace.WEST),
					block.getFace(BlockFace.DOWN),
					block.getFace(BlockFace.UP)
				};
	}

	public boolean isBlockProtected(Block block, User user)
	{
		Block[] faces = getAdjacentBlocks(block);
		boolean protect = false;
		for (Block b : faces)
		{
			int check = checkProtectionSign(b, user);
			if (check == NOT_ALLOWED)
			{
				protect = true;
			}
			if (check == ALLOWED)
			{
				return false;
			}

			if (protectedBlocks.contains(b.getType()))
			{
				Block[] faceChest = getAdjacentBlocks(b);

				for (Block a : faceChest)
				{
					check = checkProtectionSign(a, user);
					if (check == NOT_ALLOWED)
					{
						protect = true;
					}
					if (check == ALLOWED)
					{
						return false;
					}
				}
			}
		}
		return protect;
	}
		
	public static boolean isBlockProtected(Block block)
	{
		Block[] faces = getAdjacentBlocks(block);
		for (Block b : faces)
		{
			if (b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN)
			{
				Sign sign = new CraftSign(b);
				if (sign.getLine(0).equalsIgnoreCase("§1[Protection]"))
				{
					return true;
				}
			}
			if (protectedBlocks.contains(b.getType()))
			{
				Block[] faceChest = getAdjacentBlocks(b);

				for (Block a : faceChest)
				{
					if (a.getType() == Material.SIGN_POST || a.getType() == Material.WALL_SIGN)
					{
						Sign sign = new CraftSign(a);
						if (sign.getLine(0).equalsIgnoreCase("§1[Protection]"))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
