package com.earth2me.essentials.antibuild;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class EssentialsAntiBuildListener implements Listener
{
	final private transient IAntiBuild prot;
	final private transient IEssentials ess;

	public EssentialsAntiBuildListener(final IAntiBuild parent)
	{
		this.prot = parent;
		this.ess = prot.getEssentialsConnect().getEssentials();
	}

	private boolean metaPermCheck(User user, String action, Block block)
	{
		if (block == null)
		{
			return false;
		}
		return metaPermCheck(user, action, block.getTypeId(), block.getData());
	}

	private boolean metaPermCheck(User user, String action, int blockId, byte data)
	{
		final String blockPerm = "essentials.build." + action + "." + blockId;
		final String dataPerm = blockPerm + ":" + data;

		if (user.isPermissionSet(dataPerm))
		{
			return user.isAuthorized(dataPerm);
		}
		else
		{
			if (ess.getSettings().isDebug())
			{
				ess.getLogger().log(Level.INFO, "abort checking if " + user.getName() + " has " + dataPerm + " - not directly set");
			}
		}

		return user.isAuthorized(blockPerm);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(final BlockPlaceEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}

		final User user = ess.getUser(event.getPlayer());

		if (prot.getSettingBool(AntiBuildConfig.disable_build) && !user.canBuild() && !user.isAuthorized("essentials.build")
			&& !metaPermCheck(user, "place", event.getBlock()))
		{
			if (ess.getSettings().warnOnBuildDisallow())
			{
				user.sendMessage(_("buildAlert"));
			}
			event.setCancelled(true);
			return;
		}

		final Block blockPlaced = event.getBlockPlaced();
		final int id = blockPlaced.getTypeId();

		if (prot.checkProtectionItems(AntiBuildConfig.blacklist_placement, id) && !user.isAuthorized("essentials.protect.exemptplacement"))
		{
			event.setCancelled(true);
			return;
		}

		if (!user.isAuthorized("essentials.protect.alerts.notrigger")
			&& prot.checkProtectionItems(AntiBuildConfig.alert_on_placement, id))
		{
			prot.getEssentialsConnect().alert(user, blockPlaced.getType().toString(), _("alertPlaced"));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(final BlockBreakEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final User user = ess.getUser(event.getPlayer());

		if (prot.getSettingBool(AntiBuildConfig.disable_build) && !user.canBuild() && !user.isAuthorized("essentials.build")
			&& !metaPermCheck(user, "break", event.getBlock()))
		{
			if (ess.getSettings().warnOnBuildDisallow())
			{
				user.sendMessage(_("buildAlert"));
			}
			event.setCancelled(true);
			return;
		}
		final Block block = event.getBlock();
		final int typeId = block.getTypeId();

		if (prot.checkProtectionItems(AntiBuildConfig.blacklist_break, typeId)
			&& !user.isAuthorized("essentials.protect.exemptbreak"))
		{
			event.setCancelled(true);
			return;
		}
		final Material type = block.getType();

		if (!user.isAuthorized("essentials.protect.alerts.notrigger")
			&& prot.checkProtectionItems(AntiBuildConfig.alert_on_break, typeId))
		{
			prot.getEssentialsConnect().alert(user, type.toString(), _("alertBroke"));
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
			if (prot.checkProtectionItems(AntiBuildConfig.blacklist_piston, block.getTypeId()))
			{
				event.setCancelled(true);
				return;
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
		if (prot.checkProtectionItems(AntiBuildConfig.blacklist_piston, block.getTypeId()))
		{
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		// Do not return if cancelled, because the interact event has 2 cancelled states.
		final User user = ess.getUser(event.getPlayer());

		if (event.hasItem()
			&& (event.getItem().getType() == Material.WATER_BUCKET
				|| event.getItem().getType() == Material.LAVA_BUCKET)
			&& prot.getSettingBool(AntiBuildConfig.disable_build) && !user.canBuild() && !user.isAuthorized("essentials.build"))
		{
			if (ess.getSettings().warnOnBuildDisallow())
			{
				user.sendMessage(_("buildAlert"));
			}
			event.setCancelled(true);
			return;
		}

		final ItemStack item = event.getItem();
		if (item != null
			&& prot.checkProtectionItems(AntiBuildConfig.blacklist_usage, item.getTypeId())
			&& !user.isAuthorized("essentials.protect.exemptusage"))
		{
			event.setCancelled(true);
			return;
		}

		if (item != null
			&& !user.isAuthorized("essentials.protect.alerts.notrigger")
			&& prot.checkProtectionItems(AntiBuildConfig.alert_on_use, item.getTypeId()))
		{
			prot.getEssentialsConnect().alert(user, item.getType().toString(), _("alertUsed"));
		}

		if (prot.getSettingBool(AntiBuildConfig.disable_use) && !user.canBuild() && !user.isAuthorized("essentials.interact") && !user.isAuthorized("essentials.build"))
		{
			if (!metaPermCheck(user, "interact", event.getClickedBlock()))
			{
				event.setUseInteractedBlock(Result.DENY);
				if (ess.getSettings().warnOnBuildDisallow())
				{
					user.sendMessage(_("buildAlert"));
				}
			}
			if (event.hasItem() && !metaPermCheck(user, "use", event.getItem().getTypeId(), event.getItem().getData().getData()))
			{
				event.setUseItemInHand(Result.DENY);
			}
		}
	}
}
