package com.earth2me.essentials;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;


public class EssentialsBlockListener extends BlockListener
{
	private final IEssentials ess;
	private static final Logger logger = Logger.getLogger("Minecraft");
	public final static List<Material> protectedBlocks = new ArrayList<Material>(4);

	static
	{
		protectedBlocks.add(Material.CHEST);
		protectedBlocks.add(Material.BURNING_FURNACE);
		protectedBlocks.add(Material.FURNACE);
		protectedBlocks.add(Material.DISPENSER);
	}

	public EssentialsBlockListener(IEssentials ess)
	{
		this.ess = ess;
	}

	@Override
	@Deprecated
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		if (ess.getSettings().areSignsDisabled())
		{
			return;
		}
		User user = ess.getUser(event.getPlayer());
		if (protectedBlocks.contains(event.getBlock().getType()) && !user.isAuthorized("essentials.signs.protection.override"))
		{
			if (isBlockProtected(event.getBlock(), user))
			{
				event.setCancelled(true);
				user.sendMessage(Util.format("noDestroyPermission", event.getBlock().getType().toString().toLowerCase()));
				return;
			}
		}

		if (checkProtectionSign(event.getBlock(), user) != NOSIGN
			&& checkProtectionSign(event.getBlock(), user) != OWNER)
		{
			event.setCancelled(true);
			user.sendMessage(Util.format("noDestroyPermission", event.getBlock().getType().toString().toLowerCase()));
		}
	}

	@Override
	@Deprecated
	public void onSignChange(SignChangeEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		if (ess.getSettings().areSignsDisabled())
		{
			return;
		}
		User user = ess.getUser(event.getPlayer());
		String username = user.getName().substring(0, user.getName().length() > 14 ? 14 : user.getName().length());

		try
		{
			if (event.getLine(0).equalsIgnoreCase("[Protection]"))
			{
				Block block = event.getBlock();
				if (user.isAuthorized("essentials.signs.protection.create") && hasAdjacentChest(block) && !isBlockProtected(block, user))
				{
					event.setLine(0, "§1[Protection]");
				}
				else
				{
					event.setLine(0, "§4[Protection]");
				}
				event.setLine(3, username);
				return;
			}
			if (event.getLine(0).equalsIgnoreCase("[Disposal]"))
			{
				if (user.isAuthorized("essentials.signs.disposal.create"))
				{
					event.setLine(0, "§1[Disposal]");
				}
				else
				{
					event.setLine(0, "§4[Disposal]");
				}
				return;
			}
			if (event.getLine(0).equalsIgnoreCase("[Heal]"))
			{
				event.setLine(0, "§4[Heal]");
				if (user.isAuthorized("essentials.signs.heal.create"))
				{
					if (!event.getLine(1).isEmpty())
					{
						String[] l1 = event.getLine(1).split("[ :-]+", 2);
						boolean m1 = l1[0].matches("^[^0-9][\\.0-9]+");
						double q1 = Double.parseDouble(m1 ? l1[0].substring(1) : l1[0]);
						if (q1 < 1 || (!m1 && (int)q1 < 1))
						{
							throw new Exception(Util.i18n("moreThanZero"));
						}
						if (!m1)
						{
							ItemDb.get(l1[1]);
						}
						event.setLine(1, (m1 ? Util.formatCurrency(q1) : (int)q1 + " " + l1[1]));
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
				{
					event.setLine(0, "§1[Free]");
				}
				return;
			}
			if (event.getLine(0).equalsIgnoreCase("[Mail]"))
			{
				if (user.isAuthorized("essentials.signs.mail.create"))
				{
					event.setLine(0, "§1[Mail]");
				}
				else
				{
					event.setLine(0, "§4[Mail]");
				}
				return;
			}
			if (event.getLine(0).equalsIgnoreCase("[Balance]"))
			{
				if (user.isAuthorized("essentials.signs.balance.create"))
				{
					event.setLine(0, "§1[Balance]");
				}
				else
				{
					event.setLine(0, "§4[Balance]");
				}
				return;
			}
			if (event.getLine(0).equalsIgnoreCase("[Warp]"))
			{
				event.setLine(0, "§4[Warp]");
				if (user.isAuthorized("essentials.signs.warp.create"))
				{
					if (!event.getLine(3).isEmpty())
					{
						String[] l1 = event.getLine(3).split("[ :-]+", 2);
						boolean m1 = l1[0].matches("^[^0-9][\\.0-9]+");
						if (!m1 && l1.length != 2)
						{
							throw new Exception(Util.format("invalidSignLine", 4));
						}
						double q1 = Double.parseDouble(m1 ? l1[0].substring(1) : l1[0]);
						if ((int)q1 < 1)
						{
							throw new Exception(Util.i18n("moreThanZero"));
						}
						if (!m1)
						{
							ItemDb.get(l1[1]);
						}
						event.setLine(3, (m1 ? Util.formatCurrency(q1) : (int)q1 + " " + l1[1]));
					}
					if (event.getLine(1).isEmpty())
					{
						event.setLine(1, "§dWarp name!");
						return;
					}
					else
					{
						ess.getWarps().getWarp(event.getLine(1));
						if (event.getLine(2).equalsIgnoreCase("Everyone"))
						{
							event.setLine(2, "§2Everyone");
						}
						event.setLine(0, "§1[Warp]");
					}
				}
				return;
			}
			if (event.getLine(0).equalsIgnoreCase("[Time]"))
			{
				if (user.isAuthorized("essentials.signs.time.create")
					&& (event.getLine(1).equalsIgnoreCase("day")
						|| event.getLine(1).equalsIgnoreCase("night")))
				{
					event.setLine(0, "§1[Time]");
				}
				else
				{
					event.setLine(0, "§4[Time]");
				}
				return;
			}
		}
		catch (Throwable ex)
		{
			ess.showError(user, ex, "onSignChange");
		}
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		/*Block signBlock = event.getBlockAgainst();
		if (signBlock.getType() == Material.WALL_SIGN || signBlock.getType() == Material.SIGN_POST)
		{
			Sign sign = new CraftSign(signBlock);
			if (sign.getLine(0).matches("§1\\[[a-zA-Z]+\\]"))
			{
				event.setCancelled(true);
				return;
			}
		}*/
		final User user = ess.getUser(event.getPlayer());
		// Do not rely on getItemInHand();
		// http://leaky.bukkit.org/issues/663
		final ItemStack is = new ItemStack(event.getBlockPlaced().getType(), 1, (short)0, event.getBlockPlaced().getData());
		switch (is.getType())
		{
		case WOODEN_DOOR:
			is.setType(Material.WOOD_DOOR);
			is.setDurability((short)0);
			break;
		case IRON_DOOR_BLOCK:
			is.setType(Material.IRON_DOOR);
			is.setDurability((short)0);
			break;
		case SIGN_POST:
		case WALL_SIGN:
			is.setType(Material.SIGN);
			is.setDurability((short)0);
			break;
		case CROPS:
			is.setType(Material.SEEDS);
			is.setDurability((short)0);
			break;
		case CAKE_BLOCK:
			is.setType(Material.CAKE);
			is.setDurability((short)0);
			break;
		case BED_BLOCK:
			is.setType(Material.BED);
			is.setDurability((short)0);
			break;
		case REDSTONE_WIRE:
			is.setType(Material.REDSTONE);
			is.setDurability((short)0);
			break;
		case REDSTONE_TORCH_OFF:
		case REDSTONE_TORCH_ON:
			is.setType(Material.REDSTONE_TORCH_ON);
			is.setDurability((short)0);
			break;
		case DIODE_BLOCK_OFF:
		case DIODE_BLOCK_ON:
			is.setType(Material.DIODE);
			is.setDurability((short)0);
			break;
		case DOUBLE_STEP:
			is.setType(Material.STEP);
			break;
		case TORCH:
		case RAILS:
		case LADDER:
		case WOOD_STAIRS:
		case COBBLESTONE_STAIRS:
		case LEVER:
		case STONE_BUTTON:
		case FURNACE:
		case DISPENSER:
		case PUMPKIN:
		case JACK_O_LANTERN:
		case WOOD_PLATE:
		case STONE_PLATE:
			is.setDurability((short)0);
			break;
		}
		boolean unlimitedForUser = user.hasUnlimited(is);
		if (unlimitedForUser)
		{
			ess.scheduleSyncDelayedTask(
					new Runnable()
					{
						public void run()
						{
							user.getInventory().addItem(is);
							user.updateInventory();
						}
					});
		}
	}

	@Deprecated
	private boolean hasAdjacentChest(Block block)
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
	private static final int OWNER = 3;

	@Deprecated
	private int checkProtectionSign(Block block, User user)
	{
		String username = user.getName().substring(0, user.getName().length() > 14 ? 14 : user.getName().length());
		if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)
		{
			Sign sign = new CraftSign(block);
			if (sign.getLine(0).equalsIgnoreCase("§1[Protection]") && !user.isAuthorized("essentials.signs.protection.override"))
			{
				for (int i = 1; i <= 2; i++)
				{
					String line = sign.getLine(i);
					if (line.startsWith("(") && line.endsWith(")"))
					{
						line = line.substring(1, line.length() - 1);
						if (user.inGroup(line))
						{
							return ALLOWED;
						}
					}
					else if (line.equalsIgnoreCase(username))
					{
						return ALLOWED;
					}
				}
				if (sign.getLine(3).equalsIgnoreCase(username))
				{
					return OWNER;
				}
				return NOT_ALLOWED;
			}
		}
		return NOSIGN;
	}

	@Deprecated
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

	@Deprecated
	private boolean isBlockProtected(Block block, User user)
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
			if (check == ALLOWED || check == OWNER)
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
					if (check == ALLOWED || check == OWNER)
					{
						return false;
					}
				}
			}
		}
		return protect;
	}

	@Deprecated
	private static boolean isBlockProtected(Block block)
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
