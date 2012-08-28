package com.earth2me.essentials.antibuild;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
				ess.getLogger().log(Level.INFO, "DataValue perm on " + user.getName() + " is not directly set: " + dataPerm);
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
		final Block block = event.getBlockPlaced();
		final int typeId = block.getTypeId();
		final Material type = block.getType();

		if (prot.getSettingBool(AntiBuildConfig.disable_build) && !user.canBuild() && !user.isAuthorized("essentials.build")
			&& !metaPermCheck(user, "place", block))
		{
			if (ess.getSettings().warnOnBuildDisallow())
			{
				user.sendMessage(_("antiBuildPlace", type.toString()));
			}
			event.setCancelled(true);
			return;
		}

		if (prot.checkProtectionItems(AntiBuildConfig.blacklist_placement, typeId) && !user.isAuthorized("essentials.protect.exemptplacement"))
		{
			if (ess.getSettings().warnOnBuildDisallow())
			{
				user.sendMessage(_("antiBuildPlace", type.toString()));
			}
			event.setCancelled(true);
			return;
		}

		if (prot.checkProtectionItems(AntiBuildConfig.alert_on_placement, typeId)
			&& !user.isAuthorized("essentials.protect.alerts.notrigger"))
		{
			prot.getEssentialsConnect().alert(user, type.toString(), _("alertPlaced"));
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
		final Block block = event.getBlock();
		final int typeId = block.getTypeId();
		final Material type = block.getType();

		if (prot.getSettingBool(AntiBuildConfig.disable_build) && !user.canBuild() && !user.isAuthorized("essentials.build")
			&& !metaPermCheck(user, "break", block))
		{
			if (ess.getSettings().warnOnBuildDisallow())
			{
				user.sendMessage(_("antiBuildBreak", type.toString()));
			}
			event.setCancelled(true);
			return;
		}

		if (prot.checkProtectionItems(AntiBuildConfig.blacklist_break, typeId)
			&& !user.isAuthorized("essentials.protect.exemptbreak"))
		{
			if (ess.getSettings().warnOnBuildDisallow())
			{
				user.sendMessage(_("antiBuildBreak", type.toString()));
			}
			event.setCancelled(true);
			return;
		}

		if (prot.checkProtectionItems(AntiBuildConfig.alert_on_break, typeId)
			&& !user.isAuthorized("essentials.protect.alerts.notrigger"))
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
		final ItemStack item = event.getItem();

		if (item != null
			&& prot.checkProtectionItems(AntiBuildConfig.blacklist_usage, item.getTypeId())
			&& !user.isAuthorized("essentials.protect.exemptusage"))
		{
			if (ess.getSettings().warnOnBuildDisallow())
			{
				user.sendMessage(_("antiBuildUse", item.getType().toString()));
			}
			event.setCancelled(true);
			return;
		}

		if (item != null
			&& prot.checkProtectionItems(AntiBuildConfig.alert_on_use, item.getTypeId())
			&& !user.isAuthorized("essentials.protect.alerts.notrigger"))
		{
			prot.getEssentialsConnect().alert(user, item.getType().toString(), _("alertUsed"));
		}

		if (prot.getSettingBool(AntiBuildConfig.disable_use) && !user.canBuild() && !user.isAuthorized("essentials.build"))
		{
			if (event.hasItem() && !metaPermCheck(user, "interact", item.getTypeId(), item.getData().getData()))
			{
				event.setCancelled(true);
				if (ess.getSettings().warnOnBuildDisallow())
				{
					user.sendMessage(_("antiBuildUse", item.getType().toString()));
				}
				return;
			}
			if (event.hasBlock() && !metaPermCheck(user, "interact", event.getClickedBlock()))
			{
				event.setCancelled(true);
				if (ess.getSettings().warnOnBuildDisallow())
				{
					user.sendMessage(_("antiBuildInteract", event.getClickedBlock().getType().toString()));
				}
			}
		}
	}
}
