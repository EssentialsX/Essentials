package com.earth2me.essentials.protect;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;


public class EssentialsProtectBlockListener extends BlockListener
{
	private EssentialsProtect parent;

	public EssentialsProtectBlockListener(EssentialsProtect parent)
	{
		this.parent = parent;
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled()) return;
		final ItemStack item = event.getItemInHand();
		final User user = parent.ess.getUser(event.getPlayer());

		if (EssentialsProtect.playerSettings.get("protect.disable.build") && !user.canBuild())
		{
			event.setCancelled(true);
			return;
		}

		int id = event.getBlockPlaced().getTypeId();

		if (EssentialsProtect.checkProtectionItems(EssentialsProtect.blackListPlace, id) && !user.isAuthorized("essentials.protect.exemptplacement"))
		{
			event.setCancelled(true);
			return;
		}

		if (!EssentialsProtect.onPlaceAlert.isEmpty() && EssentialsProtect.onPlaceAlert.contains(String.valueOf(item.getTypeId())))
		{
			parent.alert(user, item.getType().toString(), Util.i18n("alertPlaced"));
		}

		Block blockPlaced = event.getBlockPlaced();
		Block below = blockPlaced.getFace(BlockFace.DOWN);
		if (below.getType() == Material.RAILS) {
			if (EssentialsProtect.genSettings.get("protect.protect.prevent.block-on-rail"))
			{
				if (EssentialsProtect.getStorage().isProtected(below, user.getName())) {
					event.setCancelled(true);
					return;
				}
			}
		}

		List<Block> protect = new ArrayList<Block>();
		if (blockPlaced.getType() == Material.RAILS) {
			if (EssentialsProtect.genSettings.get("protect.protect.rails"))
			{
				if (user.isAuthorized("essentials.protect"))
				{
					protect.add(blockPlaced);
					if (EssentialsProtect.genSettings.get("protect.protect.block-below"))
					{
						protect.add(blockPlaced.getFace(BlockFace.DOWN));
					}
				}
			}
		}
		if (blockPlaced.getType() == Material.SIGN_POST || blockPlaced.getType() == Material.WALL_SIGN) {
			if (EssentialsProtect.genSettings.get("protect.protect.signs"))
			{
				if (user.isAuthorized("essentials.protect"))
				{
					protect.add(blockPlaced);
					if (EssentialsProtect.genSettings.get("protect.protect.block-below"))
					{
						protect.add(event.getBlockAgainst());
					}
				}
			}
		}
		for (Block block : protect) {
			EssentialsProtect.getStorage().protectBlock(block, user.getName());
		}
	}

	@Override
	public void onBlockIgnite(BlockIgniteEvent event)
	{
		Block block = event.getBlock();
		if (block.getType() == Material.RAILS && EssentialsProtect.genSettings.get("protect.protect.rails"))
		{
			event.setCancelled(true);
			return;
		}
		if ((block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) && EssentialsProtect.genSettings.get("protect.protect.signs"))
		{
			event.setCancelled(true);
			return;
		}
		if ((event.getCause().equals(BlockIgniteEvent.IgniteCause.SPREAD)))
		{
			event.setCancelled(EssentialsProtect.guardSettings.get("protect.prevent.fire-spread"));
			return;
		}

		if (event.getCause().equals(BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL))
		{
			event.setCancelled(EssentialsProtect.guardSettings.get("protect.prevent.flint-fire"));
			return;
		}

		if (event.getCause().equals(BlockIgniteEvent.IgniteCause.LAVA))
		{
			event.setCancelled(EssentialsProtect.guardSettings.get("protect.prevent.lava-fire-spread"));
			return;
		}
	}

	@Override
	public void onBlockFromTo(BlockFromToEvent event)
	{
		if (event.isCancelled()) return;
		Block block = event.getBlock();
		Block toBlock = event.getToBlock();
		if (toBlock.getType() == Material.RAILS && EssentialsProtect.genSettings.get("protect.protect.rails"))
		{
			event.setCancelled(true);
			return;
		}
		if ((toBlock.getType() == Material.WALL_SIGN || toBlock.getType() == Material.SIGN_POST) && EssentialsProtect.genSettings.get("protect.protect.signs"))
		{
			event.setCancelled(true);
			return;
		}
		if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER)
		{
			event.setCancelled(EssentialsProtect.guardSettings.get("protect.prevent.water-flow"));
			return;
		}

		if (block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA)
		{
			event.setCancelled(EssentialsProtect.guardSettings.get("protect.prevent.lava-flow"));
			return;
		}

		if (block.getType() == Material.AIR)
		{
			event.setCancelled(EssentialsProtect.guardSettings.get("protect.prevent.water-bucket-flow"));
			return;
		}
	}

	@Override
	public void onBlockBurn(BlockBurnEvent event)
	{
		Block block = event.getBlock();
		if (block.getType() == Material.RAILS && EssentialsProtect.genSettings.get("protect.protect.rails"))
		{
			event.setCancelled(true);
			return;
		}
		if ((block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) && EssentialsProtect.genSettings.get("protect.protect.signs"))
		{
			event.setCancelled(true);
			return;
		}
		if (EssentialsProtect.guardSettings.get("protect.prevent.fire-spread"))
		{
			event.setCancelled(true);
			return;
		}
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (event.isCancelled()) return;
		User user = parent.ess.getUser(event.getPlayer());
		Block block = event.getBlock();
		if (EssentialsProtect.playerSettings.get("protect.disable.build") && !user.canBuild())
		{
			event.setCancelled(true);
			return;
		}

		if(EssentialsProtect.breakBlackList.contains(String.valueOf(block.getTypeId())) && !user.isAuthorized("essentials.protect.exemptbreak"))
		{
			event.setCancelled(true);
			return;
		}

		if (!EssentialsProtect.onBreakAlert.isEmpty() && EssentialsProtect.onBreakAlert.contains(String.valueOf(block.getTypeId())))
		{
			parent.alert(user, block.getType().toString(), Util.i18n("alertBroke"));
		}

		if (user.isAuthorized("essentials.protect.admin"))
		{
			if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.RAILS)
			{
				EssentialsProtect.getStorage().unprotectBlock(block);
				if (block.getType() == Material.RAILS || block.getType() == Material.SIGN_POST) {
					Block below = block.getFace(BlockFace.DOWN);
					EssentialsProtect.getStorage().unprotectBlock(below);
				} else {
					BlockFace[] faces = new BlockFace[] {
						BlockFace.NORTH,
						BlockFace.EAST,
						BlockFace.SOUTH,
						BlockFace.WEST
					};
					for (BlockFace blockFace : faces) {
						Block against = block.getFace(blockFace);
						EssentialsProtect.getStorage().unprotectBlock(against);
					}
				}
			}
			else
			{
				EssentialsProtect.getStorage().unprotectBlock(block);
			}
			return;
		}
		else
		{

			boolean isProtected = EssentialsProtect.getStorage().isProtected(block, user.getName());
			if (!isProtected) {
				if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.RAILS)
				{		
					EssentialsProtect.getStorage().unprotectBlock(block);
					if (block.getType() == Material.RAILS || block.getType() == Material.SIGN_POST) {
						Block below = block.getFace(BlockFace.DOWN);
						EssentialsProtect.getStorage().unprotectBlock(below);
					} else {
						BlockFace[] faces = new BlockFace[] {
							BlockFace.NORTH,
							BlockFace.EAST,
							BlockFace.SOUTH,
							BlockFace.WEST
						};
						for (BlockFace blockFace : faces) {
							Block against = block.getFace(blockFace);
							EssentialsProtect.getStorage().unprotectBlock(against);
						}
					}
				}
				else
				{
					EssentialsProtect.getStorage().unprotectBlock(block);
				}
			}
			event.setCancelled(true);
			return;
		}
	}
}
