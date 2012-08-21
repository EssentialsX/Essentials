package com.earth2me.essentials.protect;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.protect.data.IProtectedBlock;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;


public class EssentialsProtectBlockListener implements Listener
{
	final private transient IProtect prot;
	final private transient IEssentials ess;

	public EssentialsProtectBlockListener(final IProtect parent)
	{
		this.prot = parent;
		this.ess = prot.getEssentialsConnect().getEssentials();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(final BlockPlaceEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}

		final User user = ess.getUser(event.getPlayer());

		final Block blockPlaced = event.getBlockPlaced();
		final int id = blockPlaced.getTypeId();


		final Block below = blockPlaced.getRelative(BlockFace.DOWN);
		if ((below.getType() == Material.RAILS || below.getType() == Material.POWERED_RAIL || below.getType() == Material.DETECTOR_RAIL)
			&& prot.getSettingBool(ProtectConfig.prevent_block_on_rail)
			&& isProtected(below, user))
		{
			event.setCancelled(true);
			return;
		}

		final List<Block> protect = new ArrayList<Block>();
		if ((blockPlaced.getType() == Material.RAILS || blockPlaced.getType() == Material.POWERED_RAIL || blockPlaced.getType() == Material.DETECTOR_RAIL)
			&& prot.getSettingBool(ProtectConfig.protect_rails)
			&& user.isAuthorized("essentials.protect"))
		{
			protect.add(blockPlaced);
			if (prot.getSettingBool(ProtectConfig.protect_below_rails)
				&& !isProtected(blockPlaced.getRelative(BlockFace.DOWN), user))
			{
				protect.add(blockPlaced.getRelative(BlockFace.DOWN));
			}
		}
		if ((blockPlaced.getType() == Material.SIGN_POST || blockPlaced.getType() == Material.WALL_SIGN)
			&& prot.getSettingBool(ProtectConfig.protect_signs)
			&& user.isAuthorized("essentials.protect"))
		{
			protect.add(blockPlaced);
			if (prot.getSettingBool(ProtectConfig.protect_against_signs)
				&& event.getBlockAgainst().getType() != Material.SIGN_POST
				&& event.getBlockAgainst().getType() != Material.WALL_SIGN
				&& !isProtected(event.getBlockAgainst(), user))
			{
				protect.add(event.getBlockAgainst());
			}
		}
		for (Block block : protect)
		{
			prot.getStorage().protectBlock(block, user.getName());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockIgnite(BlockIgniteEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final Block block = event.getBlock();
		if ((block.getType() == Material.RAILS || block.getType() == Material.POWERED_RAIL || block.getType() == Material.DETECTOR_RAIL)
			&& prot.getSettingBool(ProtectConfig.protect_rails))
		{
			event.setCancelled(true);
			return;
		}
		if ((block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
			&& prot.getSettingBool(ProtectConfig.protect_signs))
		{
			event.setCancelled(true);
			return;
		}
		if (event.getBlock().getType() == Material.OBSIDIAN
			|| event.getBlock().getRelative(BlockFace.DOWN).getType() == Material.OBSIDIAN)
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_portal_creation));
			return;
		}

		if (event.getCause().equals(BlockIgniteEvent.IgniteCause.SPREAD))
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_fire_spread));
			return;
		}

		if (event.getCause().equals(BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL))
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_flint_fire));
			return;
		}

		if (event.getCause().equals(BlockIgniteEvent.IgniteCause.LAVA))
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_lava_fire_spread));
			return;
		}
		if (event.getCause().equals(BlockIgniteEvent.IgniteCause.LIGHTNING))
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_lightning_fire_spread));
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockFromTo(final BlockFromToEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final Block toBlock = event.getToBlock();
		if ((toBlock.getType() == Material.RAILS || toBlock.getType() == Material.POWERED_RAIL || toBlock.getType() == Material.DETECTOR_RAIL)
			&& prot.getSettingBool(ProtectConfig.protect_rails))
		{
			event.setCancelled(true);
			return;
		}
		if ((toBlock.getType() == Material.WALL_SIGN || toBlock.getType() == Material.SIGN_POST)
			&& prot.getSettingBool(ProtectConfig.protect_signs))
		{
			event.setCancelled(true);
			return;
		}

		final Block block = event.getBlock();
		if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER)
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_water_flow));
			return;
		}

		if (block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA)
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_lava_flow));
			return;
		}

		if (block.getType() == Material.AIR)
		{
			event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_water_bucket_flow));
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBurn(final BlockBurnEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final Block block = event.getBlock();
		if ((block.getType() == Material.RAILS || block.getType() == Material.POWERED_RAIL || block.getType() == Material.DETECTOR_RAIL) && prot.getSettingBool(ProtectConfig.protect_rails))
		{
			event.setCancelled(true);
			return;
		}
		if ((block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
			&& prot.getSettingBool(ProtectConfig.protect_signs))
		{
			event.setCancelled(true);
			return;
		}
		if (prot.getSettingBool(ProtectConfig.prevent_fire_spread))
		{
			event.setCancelled(true);
			return;
		}
	}
	private final static BlockFace[] faces = new BlockFace[]
	{
		BlockFace.NORTH,
		BlockFace.EAST,
		BlockFace.SOUTH,
		BlockFace.WEST,
		BlockFace.UP,
		BlockFace.DOWN,
		BlockFace.SELF
	};

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(final BlockBreakEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final User user = ess.getUser(event.getPlayer());

		final Block block = event.getBlock();
		final int typeId = block.getTypeId();

		final Material type = block.getType();

		final IProtectedBlock storage = prot.getStorage();

		if (user.isAuthorized("essentials.protect.admin"))
		{
			if (type == Material.WALL_SIGN || type == Material.SIGN_POST || type == Material.RAILS || type == Material.POWERED_RAIL || type == Material.DETECTOR_RAIL)
			{
				storage.unprotectBlock(block);
				if (type == Material.RAILS || type == Material.POWERED_RAIL || type == Material.DETECTOR_RAIL || type == Material.SIGN_POST)
				{
					final Block below = block.getRelative(BlockFace.DOWN);
					storage.unprotectBlock(below);
				}
				else
				{
					for (BlockFace blockFace : faces)
					{
						final Block against = block.getRelative(blockFace);
						storage.unprotectBlock(against);
					}
				}
			}
			else
			{
				for (BlockFace blockFace : faces)
				{
					final Block against = block.getRelative(blockFace);
					storage.unprotectBlock(against);
				}
			}
		}
		else
		{

			final boolean isProtected = isProtected(block, user);
			if (isProtected)
			{
				event.setCancelled(true);
			}
			else
			{
				if (type == Material.WALL_SIGN || type == Material.SIGN_POST || type == Material.RAILS || type == Material.POWERED_RAIL || type == Material.DETECTOR_RAIL)
				{
					storage.unprotectBlock(block);
					if (type == Material.RAILS || type == Material.POWERED_RAIL || type == Material.DETECTOR_RAIL || type == Material.SIGN_POST)
					{
						final Block below = block.getRelative(BlockFace.DOWN);
						storage.unprotectBlock(below);
					}
					else
					{
						for (BlockFace blockFace : faces)
						{
							final Block against = block.getRelative(blockFace);
							storage.unprotectBlock(against);
						}
					}
				}
				else
				{
					for (BlockFace blockFace : faces)
					{
						final Block against = block.getRelative(blockFace);
						storage.unprotectBlock(against);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonExtend(BlockPistonExtendEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		for (Block block : event.getBlocks())
		{
			if ((block.getRelative(BlockFace.UP).getType() == Material.RAILS
				 || block.getType() == Material.RAILS
				 || block.getRelative(BlockFace.UP).getType() == Material.POWERED_RAIL
				 || block.getType() == Material.POWERED_RAIL
				 || block.getRelative(BlockFace.UP).getType() == Material.DETECTOR_RAIL
				 || block.getType() == Material.DETECTOR_RAIL)
				&& prot.getSettingBool(ProtectConfig.protect_rails))
			{
				event.setCancelled(true);
				return;
			}
			if (prot.getSettingBool(ProtectConfig.protect_signs))
			{
				for (BlockFace blockFace : faces)
				{
					if (blockFace == BlockFace.DOWN)
					{
						continue;
					}
					final Block sign = block.getRelative(blockFace);
					if ((blockFace == BlockFace.UP || blockFace == BlockFace.SELF)
						&& sign.getType() == Material.SIGN_POST)
					{
						event.setCancelled(true);
						return;
					}
					if ((blockFace == BlockFace.NORTH || blockFace == BlockFace.EAST
						 || blockFace == BlockFace.SOUTH || blockFace == BlockFace.WEST
						 || blockFace == BlockFace.SELF)
						&& sign.getType() == Material.WALL_SIGN)
					{
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonRetract(BlockPistonRetractEvent event)
	{
		if (event.isCancelled() || !event.isSticky())
		{
			return;
		}
		final Block block = event.getRetractLocation().getBlock();
		if ((block.getRelative(BlockFace.UP).getType() == Material.RAILS
			 || block.getType() == Material.RAILS
			 || block.getRelative(BlockFace.UP).getType() == Material.POWERED_RAIL
			 || block.getType() == Material.POWERED_RAIL
			 || block.getRelative(BlockFace.UP).getType() == Material.DETECTOR_RAIL
			 || block.getType() == Material.DETECTOR_RAIL)
			&& prot.getSettingBool(ProtectConfig.protect_rails))
		{
			event.setCancelled(true);
			return;
		}
		if (prot.getSettingBool(ProtectConfig.protect_signs))
		{
			for (BlockFace blockFace : faces)
			{
				if (blockFace == BlockFace.DOWN)
				{
					continue;
				}
				final Block sign = block.getRelative(blockFace);
				if ((blockFace == BlockFace.UP || blockFace == BlockFace.SELF)
					&& sign.getType() == Material.SIGN_POST)
				{
					event.setCancelled(true);
					return;
				}
				if ((blockFace == BlockFace.NORTH || blockFace == BlockFace.EAST
					 || blockFace == BlockFace.SOUTH || blockFace == BlockFace.WEST
					 || blockFace == BlockFace.SELF)
					&& sign.getType() == Material.WALL_SIGN)
				{
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	private boolean isProtected(final Block block, final User user)
	{
		final Material type = block.getType();
		if (prot.getSettingBool(ProtectConfig.protect_signs))
		{
			if (type == Material.WALL_SIGN || type == Material.SIGN_POST)
			{
				return prot.getStorage().isProtected(block, user.getName());
			}
			if (prot.getSettingBool(ProtectConfig.protect_against_signs))
			{
				final Block up = block.getRelative(BlockFace.UP);
				if (up != null && up.getType() == Material.SIGN_POST)
				{
					return prot.getStorage().isProtected(block, user.getName());
				}
				final BlockFace[] directions = new BlockFace[]
				{
					BlockFace.NORTH,
					BlockFace.EAST,
					BlockFace.SOUTH,
					BlockFace.WEST
				};
				for (BlockFace blockFace : directions)
				{
					final Block signblock = block.getRelative(blockFace);
					if (signblock.getType() == Material.WALL_SIGN)
					{
						final org.bukkit.material.Sign signMat = (org.bukkit.material.Sign)signblock.getState().getData();
						if (signMat != null && signMat.getFacing() == blockFace)
						{
							return prot.getStorage().isProtected(block, user.getName());
						}
					}
				}
			}
		}
		if (prot.getSettingBool(ProtectConfig.protect_rails))
		{
			if (type == Material.RAILS || type == Material.POWERED_RAIL || type == Material.DETECTOR_RAIL)
			{
				return prot.getStorage().isProtected(block, user.getName());
			}
			if (prot.getSettingBool(ProtectConfig.protect_below_rails))
			{
				final Block up = block.getRelative(BlockFace.UP);
				if (up != null && (type == Material.RAILS || type == Material.POWERED_RAIL || type == Material.DETECTOR_RAIL))
				{
					return prot.getStorage().isProtected(block, user.getName());
				}
			}
		}
		return false;
	}
}
