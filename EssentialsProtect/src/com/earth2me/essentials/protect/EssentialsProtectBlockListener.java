package com.earth2me.essentials.protect;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.ChatColor;
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
	private int railBlockX;
	private int railBlockY;
	private int railBlockZ;
	private EssentialsProtectData spData;

	public EssentialsProtectBlockListener(EssentialsProtect parent)
	{
		this.parent = parent;
	}

	private void initialize()
	{
		if (spData != null) return;
		spData = new EssentialsProtectData();
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled()) return;
		initialize();
		ItemStack item = event.getItemInHand();
		User user = User.get(event.getPlayer());

		if (EssentialsProtect.playerSettings.get("protect.disable.build") && !user.canBuild())
		{
			if(Essentials.getSettings().warnOnBuildDisallow())
			{
				user.sendMessage(ChatColor.RED + "You are not permitted to build");
			}
			event.setCancelled(true);
			return;
		}

		Block blockPlaced = event.getBlockPlaced();
		int id = event.getBlockPlaced().getTypeId();

		if (EssentialsProtect.checkProtectionItems(EssentialsProtect.blackListPlace, id) && !user.isAuthorized("essentials.protect.exemptplacement"))
		{
			event.setCancelled(true);
			return;
		}

		if (EssentialsProtect.checkProtectionItems(EssentialsProtect.onPlaceAlert, id))
		{
			parent.alert(user, item.getType().toString(), "placed: ");
		}

		if (spData.isBlockAboveProtectedRail(blockPlaced.getFace(BlockFace.DOWN)))
		{
			if (EssentialsProtect.genSettings.get("protect.protect.prevent-block-on-rails"))
			{
				event.setCancelled(true);
				return;
			}
		}

		if (blockPlaced.getType() == Material.RAILS)
		{
			if (EssentialsProtect.genSettings.get("protect.protect.rails"))
			{
				if (user.isAuthorized("essentials.protect"))
				{
					railBlockX = blockPlaced.getX();
					railBlockY = blockPlaced.getY();
					railBlockZ = blockPlaced.getZ();

					spData.insertProtectionIntoDb(user.getWorld().getName(), user.getName(), railBlockX, railBlockY, railBlockZ);
					if (EssentialsProtect.genSettings.get("protect.protect.block-below"))
					{
						spData.insertProtectionIntoDb(user.getWorld().getName(), user.getName(), railBlockX, railBlockY - 1, railBlockZ);
					}
				}
			}
		}
		if (blockPlaced.getType() == Material.SIGN_POST || blockPlaced.getType() == Material.WALL_SIGN) {
			if (EssentialsProtect.genSettings.get("protect.protect.signs"))
			{
				if (user.isAuthorized("essentials.protect"))
				{
					int signBlockX = blockPlaced.getX();
					int signBlockY = blockPlaced.getY();
					int signBlockZ = blockPlaced.getZ();

					initialize();
					spData.insertProtectionIntoDb(user.getWorld().getName(), user.getName(), signBlockX,
												  signBlockY, signBlockZ);

					if (EssentialsProtect.genSettings.get("protect.protect.block-below"))
					{
						signBlockX = event.getBlockAgainst().getX();
						signBlockY = event.getBlockAgainst().getY();
						signBlockZ = event.getBlockAgainst().getZ();
						spData.insertProtectionIntoDb(user.getWorld().getName(), user.getName(), signBlockX,
													  signBlockY, signBlockZ);
					}
				}
			}
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
		
		if (event.getBlock().getType() == Material.OBSIDIAN || 
			event.getBlock().getFace(BlockFace.DOWN).getType() == Material.OBSIDIAN)
		{
			event.setCancelled(EssentialsProtect.guardSettings.get("protect.prevent.portal-creation"));
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
		initialize();
		User user = User.get(event.getPlayer());
		Block block = event.getBlock();
		if (EssentialsProtect.playerSettings.get("protect.disable.build") && !user.canBuild())
		{
			event.setCancelled(true);
			return;
		}

		if (EssentialsProtect.checkProtectionItems(EssentialsProtect.breakBlackList, block.getTypeId()) && !user.isAuthorized("essentials.protect.exemptbreak"))
		{
			event.setCancelled(true);
			return;
		}

		if (EssentialsProtect.checkProtectionItems(EssentialsProtect.onBreakAlert, block.getTypeId()))
		{
			parent.alert(user, block.getType().toString(), "broke: ");
		}

		if (user.isAuthorized("essentials.protect.admin"))
		{
			if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.RAILS)
			{
				spData.removeProtectionFromDB(block, true);
			}
			else
			{
				spData.removeProtectionFromDB(block);
			}
			return;
		}
		else
		{
			boolean canDestroy = spData.canDestroy(user.getWorld().getName(), user.getName(), block);
			if (canDestroy)
			{
				if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.RAILS)
				{
					spData.removeProtectionFromDB(block, true);
				}
				else
				{
					spData.removeProtectionFromDB(block);
				}
				return;
			}
			event.setCancelled(true);
			return;
		}
	}
}
