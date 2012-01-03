package com.earth2me.essentials.protect;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.protect.data.IProtectedBlock;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.*;


public class EssentialsProtectBlockListener extends BlockListener
{
	final private transient IProtect prot;
	final private transient IEssentials ess;

	public EssentialsProtectBlockListener(final IProtect parent)
	{
		this.prot = parent;
		this.ess = prot.getEssentialsConnect().getEssentials();
	}

	@Override
	public void onBlockPlace(final BlockPlaceEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}

		final IUser user = ess.getUser(event.getPlayer());
		final ProtectHolder settings = prot.getSettings();
		settings.acquireReadLock();
		try
		{
			if (!user.isAuthorized(Permissions.BUILD))
			{
				event.setCancelled(true);
				return;
			}

			final Block blockPlaced = event.getBlockPlaced();
			final int id = blockPlaced.getTypeId();

			if (!user.isAuthorized(BlockPlacePermissions.getPermission(blockPlaced.getType())))
			{
				event.setCancelled(true);
				return;
			}

			if (settings.getData().getAlertOnPlacement().contains(blockPlaced.getType()))
			{
				prot.getEssentialsConnect().alert(user, blockPlaced.getType().toString(), _("alertPlaced"));
			}

			final Block below = blockPlaced.getRelative(BlockFace.DOWN);
			if ((below.getType() == Material.RAILS || below.getType() == Material.POWERED_RAIL || below.getType() == Material.DETECTOR_RAIL)
				&& settings.getData().getSignsAndRails().isPreventBlockAboveRails()
				&& prot.getStorage().isProtected(below, user.getName()))
			{
				event.setCancelled(true);
				return;
			}

			final List<Block> protect = new ArrayList<Block>();
			if ((blockPlaced.getType() == Material.RAILS || blockPlaced.getType() == Material.POWERED_RAIL || blockPlaced.getType() == Material.DETECTOR_RAIL)
				&& settings.getData().getSignsAndRails().isRails()
				&& user.isAuthorized("essentials.protect"))
			{
				protect.add(blockPlaced);
				if (settings.getData().getSignsAndRails().isBlockBelow()
					&& !prot.getStorage().isProtected(blockPlaced.getRelative(BlockFace.DOWN), user.getName()))
				{
					protect.add(blockPlaced.getRelative(BlockFace.DOWN));
				}
			}
			if ((blockPlaced.getType() == Material.SIGN_POST || blockPlaced.getType() == Material.WALL_SIGN)
				&& settings.getData().getSignsAndRails().isSigns()
				&& user.isAuthorized("essentials.protect"))
			{
				protect.add(blockPlaced);
				if (settings.getData().getSignsAndRails().isBlockBelow()
					&& event.getBlockAgainst().getType() != Material.SIGN_POST
					&& event.getBlockAgainst().getType() != Material.WALL_SIGN
					&& !prot.getStorage().isProtected(event.getBlockAgainst(), user.getName()))
				{
					protect.add(event.getBlockAgainst());
				}
			}
			for (Block block : protect)
			{
				prot.getStorage().protectBlock(block, user.getName());
			}
		}
		finally
		{
			settings.unlock();
		}
	}

	@Override
	public void onBlockIgnite(BlockIgniteEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final ProtectHolder settings = prot.getSettings();
		settings.acquireReadLock();
		try
		{
			final Block block = event.getBlock();
			if ((block.getType() == Material.RAILS || block.getType() == Material.POWERED_RAIL || block.getType() == Material.DETECTOR_RAIL)
				&& settings.getData().getSignsAndRails().isRails())
			{
				event.setCancelled(true);
				return;
			}
			if ((block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
				&& settings.getData().getSignsAndRails().isSigns())
			{
				event.setCancelled(true);
				return;
			}
			if (event.getBlock().getType() == Material.OBSIDIAN
				|| event.getBlock().getRelative(BlockFace.DOWN).getType() == Material.OBSIDIAN)
			{
				event.setCancelled(settings.getData().getPrevent().isPortalCreation());
				return;
			}

			if (event.getCause().equals(BlockIgniteEvent.IgniteCause.SPREAD))
			{
				event.setCancelled(settings.getData().getPrevent().isFirespread());
				return;
			}

			if (event.getCause().equals(BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) && event.getPlayer() != null)
			{
				event.setCancelled(ess.getUser(event.getPlayer()).isAuthorized(Permissions.USEFLINTSTEEL));
				return;
			}

			if (event.getCause().equals(BlockIgniteEvent.IgniteCause.LAVA))
			{
				event.setCancelled(settings.getData().getPrevent().isLavaFirespread());
				return;
			}
			if (event.getCause().equals(BlockIgniteEvent.IgniteCause.LIGHTNING))
			{
				event.setCancelled(settings.getData().getPrevent().isLightningFirespread());
				return;
			}
		}
		finally
		{
			settings.unlock();
		}
	}

	@Override
	public void onBlockFromTo(final BlockFromToEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final ProtectHolder settings = prot.getSettings();
		settings.acquireReadLock();
		try
		{
			final Block toBlock = event.getToBlock();
			if ((toBlock.getType() == Material.RAILS || toBlock.getType() == Material.POWERED_RAIL || toBlock.getType() == Material.DETECTOR_RAIL)
				&& settings.getData().getSignsAndRails().isRails())
			{
				event.setCancelled(true);
				return;
			}
			if ((toBlock.getType() == Material.WALL_SIGN || toBlock.getType() == Material.SIGN_POST)
				&& settings.getData().getSignsAndRails().isSigns())
			{
				event.setCancelled(true);
				return;
			}

			final Block block = event.getBlock();
			if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER)
			{
				event.setCancelled(settings.getData().getPrevent().isWaterFlow());
				return;
			}

			if (block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA)
			{
				event.setCancelled(settings.getData().getPrevent().isLavaFlow());
				return;
			}
			// TODO: Test if this still works
			/*
			 * if (block.getType() == Material.AIR) {
			 * event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_water_bucket_flow)); return;
			}
			 */
		}
		finally
		{
			settings.unlock();
		}
	}

	@Override
	public void onBlockBurn(final BlockBurnEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final ProtectHolder settings = prot.getSettings();
		settings.acquireReadLock();
		try
		{
			final Block block = event.getBlock();
			if ((block.getType() == Material.RAILS || block.getType() == Material.POWERED_RAIL || block.getType() == Material.DETECTOR_RAIL)
				&& settings.getData().getSignsAndRails().isRails())
			{
				event.setCancelled(true);
				return;
			}
			if ((block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
				&& settings.getData().getSignsAndRails().isSigns())
			{
				event.setCancelled(true);
				return;
			}
			if (settings.getData().getPrevent().isFirespread())
			{
				event.setCancelled(true);
				return;
			}
		}
		finally
		{
			settings.unlock();
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

	@Override
	public void onBlockBreak(final BlockBreakEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final IUser user = ess.getUser(event.getPlayer());

		if (!user.isAuthorized(Permissions.BUILD))
		{
			event.setCancelled(true);
			return;
		}
		final Block block = event.getBlock();
		final int typeId = block.getTypeId();

		if (!user.isAuthorized(BlockBreakPermissions.getPermission(block.getType())))
		{
			event.setCancelled(true);
			return;
		}
		final Material type = block.getType();

		if (prot.checkProtectionItems(ProtectConfig.alert_on_break, typeId))
		{
			prot.getEssentialsConnect().alert(user, type.toString(), _("alertBroke"));
		}
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

			final boolean isProtected = storage.isProtected(block, user.getName());
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

	@Override
	public void onBlockPistonExtend(BlockPistonExtendEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		for (Block block : event.getBlocks())
		{
			if (prot.checkProtectionItems(ProtectConfig.blacklist_piston, block.getTypeId()))
			{
				event.setCancelled(true);
				return;
			}
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

	@Override
	public void onBlockPistonRetract(BlockPistonRetractEvent event)
	{
		if (event.isCancelled() || !event.isSticky())
		{
			return;
		}
		final Block block = event.getRetractLocation().getBlock();
		if (prot.checkProtectionItems(ProtectConfig.blacklist_piston, block.getTypeId()))
		{
			event.setCancelled(true);
			return;
		}
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
