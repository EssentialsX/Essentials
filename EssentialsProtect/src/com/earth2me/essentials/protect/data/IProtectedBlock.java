package com.earth2me.essentials.protect.data;

import java.util.List;
import org.bukkit.block.Block;

public interface  IProtectedBlock {
	public void clearProtections();
	public void importProtections(List<OwnedBlock> blocks);
	public List<OwnedBlock> exportProtections();
	public void protectBlock(Block block, String playerName);
	public boolean isProtected(Block block, String playerName);
	public List<String> getOwners(Block block);
	public int unprotectBlock(Block block);
	public void onPluginDeactivation();
}
