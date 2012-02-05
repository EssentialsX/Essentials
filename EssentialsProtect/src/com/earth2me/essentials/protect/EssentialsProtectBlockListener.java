package com.earth2me.essentials.protect;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.protect.data.IProtectedBlock;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
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

		final Player user = event.getPlayer();
		final ProtectHolder settings = prot.getSettings();
		settings.acquireReadLock();
		try
		{
			if (!Permissions.BUILD.isAuthorized(user))
			{
				event.setCancelled(true);
				return;
			}

			final Block blockPlaced = event.getBlockPlaced();
			final int id = blockPlaced.getTypeId();

			if (!BlockPlacePermissions.getPermission(blockPlaced.getType()).isAuthorized(user))
			{
				event.setCancelled(true);
				return;
			}

			if (!Permissions.ALERTS_NOTRIGGER.isAuthorized(user) &&
				settings.getData().getAlertOnPlacement().contains(blockPlaced.getType()))
			{
				prot.getEssentialsConnect().alert(user, blockPlaced.getType().toString(), _("alertPlaced"));
			}

			final Block below = blockPlaced.getRelative(BlockFace.DOWN);
			if ((below.getType() == Material.RAILS || below.getType() == Material.POWERED_RAIL || below.getType() == Material.DETECTOR_RAIL)
				&& settings.getData().getSignsAndRails().isPreventBlockAboveRails()
				&& isProtected(below, user, settings))
			{
				event.setCancelled(true);
				return;
			}

			final List<Block> protect = new ArrayList<Block>();
			if ((blockPlaced.getType() == Material.RAILS || blockPlaced.getType() == Material.POWERED_RAIL || blockPlaced.getType() == Material.DETECTOR_RAIL)
				&& settings.getData().getSignsAndRails().isProtectRails()
				&& Permissions.RAILS.isAuthorized(user))
			{
				protect.add(blockPlaced);
				if (settings.getData().getSignsAndRails().isBlockBelow()
					&& !isProtected(blockPlaced.getRelative(BlockFace.DOWN), user, settings))
				{
					protect.add(blockPlaced.getRelative(BlockFace.DOWN));
				}
			}
			/*if ((blockPlaced.getType() == Material.SIGN_POST || blockPlaced.getType() == Material.WALL_SIGN)
				&& settings.getData().getSignsAndRails().isProtectSigns()
				&& user.isAuthorized("essentials.protect"))
			{
				protect.add(blockPlaced);
				if (settings.getData().getSignsAndRails().isBlockBelow()
					&& event.getBlockAgainst().getType() != Material.SIGN_POST
					&& event.getBlockAgainst().getType() != Material.WALL_SIGN
					&& !isProtected(event.getBlockAgainst(), user, settings))
				{
					protect.add(event.getBlockAgainst());
				}
			}*/
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

	@EventHandler(priority = EventPriority.HIGHEST)
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
				&& settings.getData().getSignsAndRails().isProtectRails())
			{
				event.setCancelled(true);
				return;
			}
			if ((block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
				&& settings.getData().getSignsAndRails().isProtectSigns())
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
				event.setCancelled(Permissions.USEFLINTSTEEL.isAuthorized(event.getPlayer()));
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

	@EventHandler(priority = EventPriority.HIGHEST)
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
				&& settings.getData().getSignsAndRails().isProtectRails())
			{
				event.setCancelled(true);
				return;
			}
			if ((toBlock.getType() == Material.WALL_SIGN || toBlock.getType() == Material.SIGN_POST)
				&& settings.getData().getSignsAndRails().isProtectSigns())
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
			 * event.setCancelled(prot.getSettingBool(ProtectConfig.prevent_water_bucket_flow)); return; }
			 */
		}
		finally
		{
			settings.unlock();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
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
				&& settings.getData().getSignsAndRails().isProtectRails())
			{
				event.setCancelled(true);
				return;
			}
			if ((block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
				&& settings.getData().getSignsAndRails().isProtectSigns())
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(final BlockBreakEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final Player user = event.getPlayer();

		if (!Permissions.BUILD.isAuthorized(user))
		{
			event.setCancelled(true);
			return;
		}
		final Block block = event.getBlock();
		final int typeId = block.getTypeId();

		if (!BlockBreakPermissions.getPermission(block.getType()).isAuthorized(user))
		{
			event.setCancelled(true);
			return;
		}
		final ProtectHolder settings = prot.getSettings();
		settings.acquireReadLock();
		try
		{
			final Material type = block.getType();

			if (!Permissions.ALERTS_NOTRIGGER.isAuthorized(user) && settings.getData().getAlertOnBreak().contains(type))
			{
				prot.getEssentialsConnect().alert(user, type.toString(), _("alertBroke"));
			}
			final IProtectedBlock storage = prot.getStorage();

			if (Permissions.ADMIN.isAuthorized(user))
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

				final boolean isProtected = isProtected(block, user, settings);
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
		finally
		{
			settings.unlock();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonExtend(final BlockPistonExtendEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final ProtectHolder settings = prot.getSettings();
		settings.acquireReadLock();
		try
		{
			for (Block block : event.getBlocks())
			{
				if (settings.getData().getPrevent().getPistonPush().contains(block.getType()))
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
					&& settings.getData().getSignsAndRails().isProtectRails())
				{
					event.setCancelled(true);
					return;
				}
				if (settings.getData().getSignsAndRails().isProtectSigns())
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
		finally
		{
			settings.unlock();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonRetract(final BlockPistonRetractEvent event)
	{
		if (event.isCancelled() || !event.isSticky())
		{
			return;
		}
		final ProtectHolder settings = prot.getSettings();
		settings.acquireReadLock();
		try
		{
			final Block block = event.getRetractLocation().getBlock();
			if (settings.getData().getPrevent().getPistonPush().contains(block.getType()))
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
				&& settings.getData().getSignsAndRails().isProtectRails())
			{
				event.setCancelled(true);
				return;
			}
			if (settings.getData().getSignsAndRails().isProtectSigns())
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

		finally
		{
			settings.unlock();
		}
	}

	private boolean isProtected(final Block block, final Player user, final ProtectHolder settings)
	{
		final Material type = block.getType();
		if (settings.getData().getSignsAndRails().isProtectSigns())
		{
			if (type == Material.WALL_SIGN || type == Material.SIGN_POST)
			{
				return prot.getStorage().isProtected(block, user.getName());
			}

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
		if (settings.getData().getSignsAndRails().isProtectRails())
		{
			if (type == Material.RAILS || type == Material.POWERED_RAIL || type == Material.DETECTOR_RAIL)
			{
				return prot.getStorage().isProtected(block, user.getName());
			}
			if (settings.getData().getSignsAndRails().isBlockBelow())
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
