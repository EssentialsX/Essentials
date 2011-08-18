package com.earth2me.essentials.signs;

import com.earth2me.essentials.IEssentials;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;


public class SignEntityListener extends EntityListener
{
	private final transient IEssentials ess;

	public SignEntityListener(final IEssentials ess)
	{
		this.ess = ess;
	}

	@Override
	public void onEntityExplode(final EntityExplodeEvent event)
	{
		for (Block block : event.blockList())
		{
			if (((block.getType() == Material.WALL_SIGN
				  || block.getType() == Material.SIGN_POST)
				 && EssentialsSign.isValidSign(new EssentialsSign.BlockSign(block)))
				|| EssentialsSign.checkIfBlockBreaksSigns(block))
			{
				event.setCancelled(true);
				return;
			}
			for (Signs signs : Signs.values())
			{
				final EssentialsSign sign = signs.getSign();
				if (sign.getBlocks().contains(block.getType()))
				{
					event.setCancelled(!sign.onBlockExplode(block, ess));
					return;
				}
			}
		}
	}
}
