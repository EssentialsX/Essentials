package com.earth2me.essentials.signs;

import com.earth2me.essentials.IEssentials;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;


public class SignBlockListener extends BlockListener
{
	private final transient IEssentials ess;

	public SignBlockListener(IEssentials ess)
	{
		this.ess = ess;
	}

	@Override
	public void onBlockBreak(final BlockBreakEvent event)
	{
		if (event.isCancelled() || ess.getSettings().areSignsDisabled())
		{
			return;
		}

		if (protectSignsAndBlocks(event.getBlock(), event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}

	public boolean protectSignsAndBlocks(final Block block, final Player player)
	{
		final int mat = block.getTypeId();
		if (mat == Material.SIGN_POST.getId() || mat == Material.WALL_SIGN.getId())
		{
			final Sign csign = new CraftSign(block);
			for (Signs signs : Signs.values())
			{
				final EssentialsSign sign = signs.getSign();
				if (csign.getLine(0).equalsIgnoreCase(sign.getSuccessName())
					&& !sign.onSignBreak(block, player, ess))
				{
					return true;
				}
			}
		}
		else
		{
			// prevent any signs be broken by destroying the block they are attached to
			if (EssentialsSign.checkIfBlockBreaksSigns(block))
			{
				return true;
			}
			for (Signs signs : Signs.values())
			{
				final EssentialsSign sign = signs.getSign();
				if (sign.getBlocks().contains(block.getType())
					&& !sign.onBlockBreak(block, player, ess))
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onSignChange(final SignChangeEvent event)
	{
		if (event.isCancelled() || ess.getSettings().areSignsDisabled())
		{
			return;
		}
		for (Signs signs : Signs.values())
		{
			final EssentialsSign sign = signs.getSign();
			if (event.getLine(0).equalsIgnoreCase(sign.getTemplateName()))
			{
				event.setCancelled(!sign.onSignCreate(event, ess));
				return;
			}
		}
	}

	@Override
	public void onBlockBurn(final BlockBurnEvent event)
	{
		if (event.isCancelled() || ess.getSettings().areSignsDisabled())
		{
			return;
		}

		Block block = event.getBlock();
		if ((block.getType() == Material.WALL_SIGN
			 || block.getType() == Material.SIGN_POST
			 || EssentialsSign.checkIfBlockBreaksSigns(block)))
		{
			event.setCancelled(true);
			return;
		}
		for (Signs signs : Signs.values())
		{
			final EssentialsSign sign = signs.getSign();
			if (sign.getBlocks().contains(block.getType()))
			{
				event.setCancelled(!sign.onBlockBurn(block, ess));
				return;
			}
		}
	}

	@Override
	public void onBlockIgnite(final BlockIgniteEvent event)
	{
		if (event.isCancelled() || ess.getSettings().areSignsDisabled())
		{
			return;
		}

		if (protectSignsAndBlocks(event.getBlock(), event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}
}
