package com.earth2me.essentials.signs;

import com.earth2me.essentials.api.ChargeException;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import java.util.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;


public class SignProtection extends EssentialsSign
{
	private final transient Set<Material> protectedBlocks = EnumSet.noneOf(Material.class);

	public SignProtection()
	{
		super("Protection");
		protectedBlocks.add(Material.CHEST);
		protectedBlocks.add(Material.BURNING_FURNACE);
		protectedBlocks.add(Material.FURNACE);
		protectedBlocks.add(Material.DISPENSER);
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final IUser player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		sign.setLine(3, "§4" + username);
		if (hasAdjacentBlock(sign.getBlock()))
		{
			final SignProtectionState state = isBlockProtected(sign.getBlock(), player, username, true, ess);
			if (state == SignProtectionState.NOSIGN || state == SignProtectionState.OWNER
				|| SignsPermissions.PROTECTION_OVERRIDE.isAuthorized(player))
			{
				sign.setLine(3, "§1" + username);
				return true;
			}
		}
		player.sendMessage(_("signProtectInvalidLocation"));
		return false;
	}

	@Override
	protected boolean onSignBreak(final ISign sign, final IUser player, final String username, final IEssentials ess) throws SignException
	{
		final SignProtectionState state = checkProtectionSign(sign, player, username, ess);
		return state == SignProtectionState.OWNER;
	}

	public boolean hasAdjacentBlock(final Block block, final Block... ignoredBlocks)
	{
		final Block[] faces = getAdjacentBlocks(block);
		for (Block b : faces)
		{
			for (Block ignoredBlock : ignoredBlocks)
			{
				if (b.getLocation().equals(ignoredBlock.getLocation()))
				{
					continue;
				}
			}
			if (protectedBlocks.contains(b.getType()))
			{
				return true;
			}
		}
		return false;
	}

	private void checkIfSignsAreBroken(final Block block, final IUser player, final String username, final IEssentials ess)
	{
		final Map<Location, SignProtectionState> signs = getConnectedSigns(block, player, username, false, ess);
		for (Map.Entry<Location, SignProtectionState> entry : signs.entrySet())
		{
			if (entry.getValue() != SignProtectionState.NOSIGN)
			{
				final Block sign = entry.getKey().getBlock();
				if (!hasAdjacentBlock(sign, block))
				{
					block.setType(Material.AIR);
					final Trade trade = new Trade(new ItemStack(Material.SIGN, 1), ess);
					trade.pay(player);
				}
			}
		}
	}

	private Map<Location, SignProtectionState> getConnectedSigns(final Block block, final IUser user, final String username, boolean secure, final IEssentials ess)
	{
		final Map<Location, SignProtectionState> signs = new HashMap<Location, SignProtectionState>();
		getConnectedSigns(block, signs, user, username, secure ? 4 : 2, ess);
		return signs;
	}

	private void getConnectedSigns(final Block block, final Map<Location, SignProtectionState> signs, final IUser user, final String username, final int depth, final IEssentials ess)
	{
		final Block[] faces = getAdjacentBlocks(block);
		for (Block b : faces)
		{
			final Location loc = b.getLocation();
			if (signs.containsKey(loc))
			{
				continue;
			}
			final SignProtectionState check = checkProtectionSign(b, user, username, ess);
			signs.put(loc, check);

			if (protectedBlocks.contains(b.getType()) && depth > 0)
			{
				getConnectedSigns(b, signs, user, username, depth - 1, ess);
			}
		}
	}


	public enum SignProtectionState
	{
		NOT_ALLOWED, ALLOWED, NOSIGN, OWNER
	}

	private SignProtectionState checkProtectionSign(final Block block, final IUser user, final String username, final IEssentials ess)
	{
		if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)
		{
			final BlockSign sign = new BlockSign(block);
			if (sign.getLine(0).equals(this.getSuccessName()))
			{
				return checkProtectionSign(sign, user, username, ess);
			}
		}
		return SignProtectionState.NOSIGN;
	}

	private SignProtectionState checkProtectionSign(final ISign sign, final IUser user, final String username, final IEssentials ess)
	{
		if (user == null || username == null)
		{
			return SignProtectionState.NOT_ALLOWED;
		}
		if (SignsPermissions.PROTECTION_OVERRIDE.isAuthorized(user))
		{
			return SignProtectionState.OWNER;
		}
		if (Util.stripColor(sign.getLine(3)).equalsIgnoreCase(username))
		{
			return SignProtectionState.OWNER;
		}
		for (int i = 1; i <= 2; i++)
		{
			final String line = sign.getLine(i);
			if (line.startsWith("(") && line.endsWith(")") && ess.getGroups().inGroup(user, line.substring(1, line.length() - 1)))
			{
				return SignProtectionState.ALLOWED;
			}
			else if (line.equalsIgnoreCase(username))
			{
				return SignProtectionState.ALLOWED;
			}
		}
		return SignProtectionState.NOT_ALLOWED;
	}

	private Block[] getAdjacentBlocks(final Block block)
	{
		return new Block[]
				{
					block.getRelative(BlockFace.NORTH),
					block.getRelative(BlockFace.SOUTH),
					block.getRelative(BlockFace.EAST),
					block.getRelative(BlockFace.WEST),
					block.getRelative(BlockFace.DOWN),
					block.getRelative(BlockFace.UP)
				};
	}

	public SignProtectionState isBlockProtected(final Block block, final IUser user, final String username, boolean secure, final IEssentials ess)
	{
		final Map<Location, SignProtectionState> signs = getConnectedSigns(block, user, username, secure, ess);
		SignProtectionState retstate = SignProtectionState.NOSIGN;
		for (SignProtectionState state : signs.values())
		{
			if (state == SignProtectionState.ALLOWED)
			{
				retstate = state;
			}
			else if (state == SignProtectionState.NOT_ALLOWED && retstate != SignProtectionState.ALLOWED)
			{
				retstate = state;
			}
		}
		if (!secure || retstate == SignProtectionState.NOSIGN)
		{
			for (SignProtectionState state : signs.values())
			{
				if (state == SignProtectionState.OWNER)
				{
					return state;
				}
			}
		}
		return retstate;
	}

	public boolean isBlockProtected(final Block block)
	{
		final Block[] faces = getAdjacentBlocks(block);
		for (Block b : faces)
		{
			if (b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN)
			{
				final Sign sign = (Sign)b.getState();
				if (sign.getLine(0).equalsIgnoreCase("§1[Protection]"))
				{
					return true;
				}
			}
			if (protectedBlocks.contains(b.getType()))
			{
				final Block[] faceChest = getAdjacentBlocks(b);

				for (Block a : faceChest)
				{
					if (a.getType() == Material.SIGN_POST || a.getType() == Material.WALL_SIGN)
					{
						final Sign sign = (Sign)a.getState();
						if (sign.getLine(0).equalsIgnoreCase("§1[Protection]"))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public Set<Material> getBlocks()
	{
		return protectedBlocks;
	}

	@Override
	protected boolean onBlockPlace(final Block block, final IUser player, final String username, final IEssentials ess) throws SignException
	{
		for (Block adjBlock : getAdjacentBlocks(block))
		{
			final SignProtectionState state = isBlockProtected(adjBlock, player, username, true, ess);

			if ((state == SignProtectionState.ALLOWED || state == SignProtectionState.NOT_ALLOWED)
				&& !SignsPermissions.PROTECTION_OVERRIDE.isAuthorized(player))
			{
				player.sendMessage(_("noPlacePermission", block.getType().toString().toLowerCase(Locale.ENGLISH)));
				return false;
			}
		}
		return true;

	}

	@Override
	protected boolean onBlockInteract(final Block block, final IUser player, final String username, final IEssentials ess) throws SignException
	{
		final SignProtectionState state = isBlockProtected(block, player, username, false, ess);

		if (state == SignProtectionState.OWNER || state == SignProtectionState.NOSIGN || state == SignProtectionState.ALLOWED)
		{
			return true;
		}

		if (state == SignProtectionState.NOT_ALLOWED
			&& SignsPermissions.PROTECTION_OVERRIDE.isAuthorized(player))
		{
			return true;
		}


		player.sendMessage(_("noAccessPermission", block.getType().toString().toLowerCase(Locale.ENGLISH)));
		return false;
	}

	@Override
	protected boolean onBlockBreak(final Block block, final IUser player, final String username, final IEssentials ess) throws SignException
	{
		final SignProtectionState state = isBlockProtected(block, player, username, false, ess);

		if (state == SignProtectionState.OWNER || state == SignProtectionState.NOSIGN)
		{
			checkIfSignsAreBroken(block, player, username, ess);
			return true;
		}

		if ((state == SignProtectionState.ALLOWED || state == SignProtectionState.NOT_ALLOWED)
			&& SignsPermissions.PROTECTION_OVERRIDE.isAuthorized(player))
		{
			checkIfSignsAreBroken(block, player, username, ess);
			return true;
		}


		player.sendMessage(_("noDestroyPermission", block.getType().toString().toLowerCase(Locale.ENGLISH)));
		return false;
	}

	@Override
	public boolean onBlockBreak(final Block block, final IEssentials ess)
	{
		final SignProtectionState state = isBlockProtected(block, null, null, false, ess);

		return state == SignProtectionState.NOSIGN;
	}

	@Override
	public boolean onBlockExplode(final Block block, final IEssentials ess)
	{
		final SignProtectionState state = isBlockProtected(block, null, null, false, ess);

		return state == SignProtectionState.NOSIGN;
	}

	@Override
	public boolean onBlockBurn(final Block block, final IEssentials ess)
	{
		final SignProtectionState state = isBlockProtected(block, null, null, false, ess);

		return state == SignProtectionState.NOSIGN;
	}

	@Override
	public boolean onBlockIgnite(final Block block, final IEssentials ess)
	{
		final SignProtectionState state = isBlockProtected(block, null, null, false, ess);

		return state == SignProtectionState.NOSIGN;
	}

	@Override
	public boolean onBlockPush(final Block block, final IEssentials ess)
	{
		final SignProtectionState state = isBlockProtected(block, null, null, false, ess);

		return state == SignProtectionState.NOSIGN;
	}
}
