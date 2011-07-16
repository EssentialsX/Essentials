package com.earth2me.essentials;

import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;


/**
 * Original authors: toi & Raphfrk
 */
public class TargetBlock
{
	private transient final Location location;
	private transient final double viewHeight;
	private transient final int maxDistance;
	private transient final int[] blockToIgnore;
	private transient final double checkDistance;
	private transient double curDistance;
	private transient double targetPositionX;
	private transient double targetPositionY;
	private transient double targetPositionZ;
	private transient int itargetPositionX;
	private transient int itargetPositionY;
	private transient int itargetPositionZ;
	private transient int prevPositionX;
	private transient int prevPositionY;
	private transient int prevPositionZ;
	private transient final double offsetX;
	private transient final double offsetY;
	private transient final double offsetZ;

	/**
	 * Constructor requiring a player, uses default values
	 * 
	 * @param player Player to work with
	 */
	public TargetBlock(final Player player)
	{
		this(player.getLocation(), 300, 1.65, 0.2, null);
	}

	/**
	 * Constructor requiring a location, uses default values
	 * 
	 * @param loc Location to work with
	 */
	public TargetBlock(final Location loc)
	{
		this(loc, 300, 0, 0.2, null);
	}

	/**
	 * Constructor requiring a player, max distance and a checking distance
	 * 
	 * @param player Player to work with
	 * @param maxDistance How far it checks for blocks
	 * @param checkDistance How often to check for blocks, the smaller the more precise
	 */
	public TargetBlock(final Player player, final int maxDistance, final double checkDistance)
	{
		this(player.getLocation(), maxDistance, 1.65, checkDistance, null);
	}

	/**
	 * Constructor requiring a location, max distance and a checking distance
	 * 
	 * @param loc What location to work with
	 * @param maxDistance How far it checks for blocks
	 * @param checkDistance How often to check for blocks, the smaller the more precise
	 */
	public TargetBlock(final Location loc, final int maxDistance, final double checkDistance)
	{
		this(loc, maxDistance, 0, checkDistance, null);
	}

	/**
	 * Constructor requiring a player, max distance, checking distance and an array of blocks to ignore
	 * 
	 * @param player What player to work with
	 * @param maxDistance How far it checks for blocks
	 * @param checkDistance How often to check for blocks, the smaller the more precise
	 * @param blocksToIgnore Integer array of what block ids to ignore while checking for viable targets
	 */
	public TargetBlock(final Player player, final int maxDistance, final double checkDistance, final int[] blocksToIgnore)
	{
		this(player.getLocation(), maxDistance, 1.65, checkDistance, blocksToIgnore);
	}

	/**
	 * Constructor requiring a location, max distance, checking distance and an array of blocks to ignore
	 * 
	 * @param loc What location to work with
	 * @param maxDistance How far it checks for blocks
	 * @param checkDistance How often to check for blocks, the smaller the more precise
	 * @param blocksToIgnore Array of what block ids to ignore while checking for viable targets
	 */
	public TargetBlock(final Location loc, final int maxDistance, final double checkDistance, final int[] blocksToIgnore)
	{
		this(loc, maxDistance, 0, checkDistance, blocksToIgnore);
	}

	/**
	 * Constructor requiring a player, max distance, checking distance and an array of blocks to ignore
	 * 
	 * @param player What player to work with
	 * @param maxDistance How far it checks for blocks
	 * @param checkDistance How often to check for blocks, the smaller the more precise
	 * @param blocksToIgnore String ArrayList of what block ids to ignore while checking for viable targets
	 */
	public TargetBlock(final Player player, final int maxDistance, final double checkDistance, final List<String> blocksToIgnore)
	{
		this(player.getLocation(), maxDistance, 1.65, checkDistance, TargetBlock.convertStringArraytoIntArray(blocksToIgnore));
	}

	/**
	 * Constructor requiring a location, max distance, checking distance and an array of blocks to ignore
	 * 
	 * @param loc What location to work with
	 * @param maxDistance How far it checks for blocks
	 * @param checkDistance How often to check for blocks, the smaller the more precise
	 * @param blocksToIgnore String ArrayList of what block ids to ignore while checking for viable targets
	 */
	public TargetBlock(final Location loc, final int maxDistance, final double checkDistance, final List<String> blocksToIgnore)
	{
		this(loc, maxDistance, 0, checkDistance, TargetBlock.convertStringArraytoIntArray(blocksToIgnore));
	}

	/**
	 * Set the values, all constructors uses this function
	 * 
	 * @param loc Location of the view
	 * @param maxDistance How far it checks for blocks
	 * @param viewPos Where the view is positioned in y-axis
	 * @param checkDistance How often to check for blocks, the smaller the more precise
	 * @param blocksToIgnore Ids of blocks to ignore while checking for viable targets
	 */
	private TargetBlock(final Location loc, final int maxDistance, final double viewHeight, final double checkDistance, final int[] blocksToIgnore)
	{
		this.location = loc;
		this.maxDistance = maxDistance;
		this.viewHeight = viewHeight;
		this.checkDistance = checkDistance;
		if (blocksToIgnore == null || blocksToIgnore.length == 0)
		{
			this.blockToIgnore = new int[0];
		}
		else
		{
			this.blockToIgnore = new int[blocksToIgnore.length];
			System.arraycopy(blocksToIgnore, 0, this.blockToIgnore, 0, this.blockToIgnore.length);
		}

		final double xRotation = (loc.getYaw() + 90) % 360;
		final double yRotation = loc.getPitch() * -1;

		final double hypotenuse = (checkDistance * Math.cos(Math.toRadians(yRotation)));
		offsetX = hypotenuse * Math.cos(Math.toRadians(xRotation));
		offsetY = checkDistance * Math.sin(Math.toRadians(yRotation));
		offsetZ = hypotenuse * Math.sin(Math.toRadians(xRotation));

		reset();
	}

	/**
	 * Call this to reset checking position to allow you to check for a new target with the same TargetBlock instance.
	 */
	public final void reset()
	{
		targetPositionX = location.getX();
		targetPositionY = location.getY() + viewHeight;
		targetPositionZ = location.getZ();
		itargetPositionX = (int)Math.floor(targetPositionX);
		itargetPositionY = (int)Math.floor(targetPositionY);
		itargetPositionZ = (int)Math.floor(targetPositionZ);
		prevPositionX = itargetPositionX;
		prevPositionY = itargetPositionY;
		prevPositionZ = itargetPositionZ;
		this.curDistance = 0;
	}

	/**
	 * Gets the distance to a block. Measures from the block underneath the player to the targetblock
	 * Should only be used when passing player as an constructor parameter
	 * 
	 * @return double
	 */
	public double getDistanceToBlock()
	{
		final double blockUnderPlayerX = Math.floor(location.getX() + 0.5);
		final double blockUnderPlayerY = Math.floor(location.getY() - 0.5);
		final double blockUnderPlayerZ = Math.floor(location.getZ() + 0.5);

		final Block block = getTargetBlock();
		final double distX = block.getX() - blockUnderPlayerX;
		final double distY = block.getY() - blockUnderPlayerY;
		final double distZ = block.getZ() - blockUnderPlayerZ;

		return Math.sqrt(distX*distX + distY*distY + distZ*distZ);
	}

	/**
	 * Gets the rounded distance to a block. Measures from the block underneath the player to the targetblock
	 * Should only be used when passing player as an constructor parameter
	 * 
	 * @return int
	 */
	public int getDistanceToBlockRounded()
	{
		return (int)Math.round(getDistanceToBlock());
	}

	/**
	 * Gets the floored x distance to a block.
	 * 
	 * @return int
	 */
	public int getXDistanceToBlock()
	{
		return (int)Math.floor(getTargetBlock().getX() - location.getBlockX() + 0.5);
	}

	/**
	 * Gets the floored y distance to a block
	 * 
	 * @return int
	 */
	public int getYDistanceToBlock()
	{
		return (int)Math.floor(getTargetBlock().getY() - location.getBlockY() + viewHeight);
	}

	/**
	 * Gets the floored z distance to a block
	 * 
	 * @return int
	 */
	public int getZDistanceToBlock()
	{
		return (int)Math.floor(getTargetBlock().getZ() - location.getBlockZ() + 0.5);
	}

	/**
	 * Returns the block at the sight. Returns null if out of range or if no viable target was found
	 * 
	 * @return Block
	 */
	public Block getTargetBlock()
	{
		this.reset();
		Block block;
		do
		{
			block = getNextBlock();
		}
		while (block != null && ((block.getTypeId() == 0) || this.blockIsIgnored(block.getTypeId())));

		return block;
	}

	/**
	 * Sets the type of the block at the sight. Returns false if the block wasn't set.
	 * 
	 * @param typeID ID of type to set the block to
	 * @return boolean
	 */
	public boolean setTargetBlock(final int typeID)
	{
		return setTargetBlock(Material.getMaterial(typeID));
	}

	/**
	 * Sets the type of the block at the sight. Returns false if the block wasn't set.
	 * 
	 * @param type Material to set the block to
	 * @return boolean
	 */
	@SuppressWarnings("empty-statement")
	public boolean setTargetBlock(final Material type)
	{
		if (type == null)
		{
			return false;
		}
		final Block block = getTargetBlock();
		if (block != null)
		{
			block.setType(type);
			return true;
		}
		return false;
	}

	/**
	 * Sets the type of the block at the sight. Returns false if the block wasn't set.
	 * Observe! At the moment this function is using the built-in enumerator function .valueOf(String) but would preferably be changed to smarter function, when implemented
	 * 
	 * @param type Name of type to set the block to
	 * @return boolean
	 */
	public boolean setTargetBlock(final String type)
	{
		return setTargetBlock(Material.valueOf(type));
	}

	/**
	 * Returns the block attached to the face at the sight. Returns null if out of range or if no viable target was found
	 * 
	 * @return Block
	 */
	public Block getFaceBlock()
	{
		final Block block = getTargetBlock();
		if (block == null)
		{
			return null;
		}
		return getPreviousBlock();
	}

	/**
	 * Sets the type of the block attached to the face at the sight. Returns false if the block wasn't set.
	 * 
	 * @param typeID
	 * @return boolean
	 */
	public boolean setFaceBlock(final int typeID)
	{
		return setFaceBlock(Material.getMaterial(typeID));
	}

	/**
	 * Sets the type of the block attached to the face at the sight. Returns false if the block wasn't set.
	 * 
	 * @param type
	 * @return boolean
	 */
	public boolean setFaceBlock(final Material type)
	{
		if (type == null)
		{
			return false;
		}
		if (getCurrentBlock() != null)
		{
			final Block blk = location.getWorld().getBlockAt(prevPositionX, prevPositionY, prevPositionZ);
			blk.setType(type);
			return true;
		}
		return false;
	}

	/**
	 * Sets the type of the block attached to the face at the sight. Returns false if the block wasn't set.
	 * Observe! At the moment this function is using the built-in enumerator function .valueOf(String) but would preferably be changed to smarter function, when implemented
	 * 
	 * @param type
	 * @return boolean
	 */
	public boolean setFaceBlock(final String type)
	{
		return setFaceBlock(Material.valueOf(type));
	}

	/**
	 * Get next block
	 * 
	 * @return Block
	 */
	public Block getNextBlock()
	{
		prevPositionX = itargetPositionX;
		prevPositionY = itargetPositionY;
		prevPositionZ = itargetPositionZ;
		do
		{
			curDistance += checkDistance;

			targetPositionX += offsetX;
			targetPositionY += offsetY;
			targetPositionZ += offsetZ;
			itargetPositionX = (int)Math.floor(targetPositionX);
			itargetPositionY = (int)Math.floor(targetPositionY);
			itargetPositionZ = (int)Math.floor(targetPositionZ);
		}
		while (curDistance <= maxDistance && itargetPositionX == prevPositionX && itargetPositionY == prevPositionY && itargetPositionZ == prevPositionZ);
		if (curDistance > maxDistance)
		{
			return null;
		}

		return this.location.getWorld().getBlockAt(itargetPositionX, itargetPositionY, itargetPositionZ);
	}

	/**
	 * Returns the current block along the line of vision
	 * 
	 * @return Block
	 */
	public Block getCurrentBlock()
	{
		Block block;
		if (curDistance <= maxDistance)
		{
			block = this.location.getWorld().getBlockAt(itargetPositionX, itargetPositionY, itargetPositionZ);
		}
		else
		{
			block = null;
		}
		return block;
	}

	/**
	 * Sets current block type. Returns false if the block wasn't set.
	 * 
	 * @param typeID
	 */
	public boolean setCurrentBlock(final int typeID)
	{
		return setCurrentBlock(Material.getMaterial(typeID));
	}

	/**
	 * Sets current block type. Returns false if the block wasn't set.
	 * 
	 * @param type
	 */
	public boolean setCurrentBlock(final Material type)
	{
		final Block blk = getCurrentBlock();
		if (blk != null && type != null)
		{
			blk.setType(type);
			return true;
		}
		return false;
	}

	/**
	 * Sets current block type. Returns false if the block wasn't set.
	 * Observe! At the moment this function is using the built-in enumerator function .valueOf(String) but would preferably be changed to smarter function, when implemented
	 * 
	 * @param type
	 */
	public boolean setCurrentBlock(final String type)
	{
		return setCurrentBlock(Material.valueOf(type));
	}

	/**
	 * Returns the previous block in the aimed path
	 * 
	 * @return Block
	 */
	public Block getPreviousBlock()
	{
		return this.location.getWorld().getBlockAt(prevPositionX, prevPositionY, prevPositionZ);
	}

	/**
	 * Sets previous block type id. Returns false if the block wasn't set.
	 * 
	 * @param typeID
	 */
	public boolean setPreviousBlock(final int typeID)
	{
		return setPreviousBlock(Material.getMaterial(typeID));
	}

	/**
	 * Sets previous block type id. Returns false if the block wasn't set.
	 * 
	 * @param type
	 */
	public boolean setPreviousBlock(final Material type)
	{
		final Block blk = getPreviousBlock();
		if (blk != null && type != null)
		{
			blk.setType(type);
			return true;
		}
		return false;
	}

	/**
	 * Sets previous block type id. Returns false if the block wasn't set.
	 * Observe! At the moment this function is using the built-in enumerator function .valueOf(String) but would preferably be changed to smarter function, when implemented
	 * 
	 * @param type
	 */
	public boolean setPreviousBlock(final String type)
	{
		return setPreviousBlock(Material.valueOf(type));
	}

	private static int[] convertStringArraytoIntArray(final List<String> array)
	{
		final int intarray[] = new int[array == null ? 0 : array.size()];
		for (int i = 0; i < intarray.length; i++)
		{
			try
			{
				intarray[i] = Integer.parseInt(array.get(i));
			}
			catch (NumberFormatException nfe)
			{
			}
		}
		return intarray;
	}

	private boolean blockIsIgnored(final int value)
	{
		for (int i : this.blockToIgnore)
		{
			if (i == value)
			{
				return true;
			}
		}
		return false;
	}
}