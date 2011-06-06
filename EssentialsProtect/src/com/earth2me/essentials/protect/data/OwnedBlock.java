package com.earth2me.essentials.protect.data;

public class OwnedBlock {
	final int x;
	final int y;
	final int z;
	final String world;
	final String playerName;

	public OwnedBlock(int x, int y, int z, String world, String playerName)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.playerName = playerName;
	}
}
