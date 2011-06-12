package com.earth2me.essentials.signs;

import com.earth2me.essentials.Charge;
import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.ItemDb;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class EssentialsSign
{
	protected transient final String signName;
	private static final String FORMAT_SUCCESS = "§1[%s]";
	private static final String FORMAT_FAIL = "§4[%s]";

	public EssentialsSign(final String signName)
	{
		this.signName = signName;
	}

	public final boolean onSignCreate(final SignChangeEvent event, final IEssentials ess)
	{
		final ISign sign = new EventSign(event);
		sign.setLine(0, String.format(FORMAT_FAIL, this.signName));
		final User user = ess.getUser(event.getPlayer());
		if (!(user.isAuthorized("essentials.signs." + signName.toLowerCase() + ".create")
			  || user.isAuthorized("essentials.signs.create." + signName.toLowerCase())))
		{
			return false;
		}
		boolean ret;
		try
		{
			ret = onSignCreate(sign, user, getUsername(user), ess);
		}
		catch (SignException ex)
		{
			ess.showError(user, ex, signName);
			ret = false;
		}
		if (ret)
		{
			sign.setLine(0, String.format(FORMAT_SUCCESS, this.signName));
		}
		return ret;
	}

	private String getUsername(final User user)
	{
		return user.getName().substring(0, user.getName().length() > 14 ? 14 : user.getName().length());
	}

	public final boolean onSignInteract(final PlayerInteractEvent event, final IEssentials ess)
	{
		final ISign sign = new BlockSign(event.getClickedBlock());
		final User user = ess.getUser(event.getPlayer());
		try
		{
			return (user.isAuthorized("essentials.signs." + signName.toLowerCase() + ".use")
					|| user.isAuthorized("essentials.signs.use." + signName.toLowerCase()))
				   && onSignInteract(sign, user, getUsername(user), ess);
		}
		catch (ChargeException ex)
		{
			ess.showError(user, ex, signName);
			return false;
		}
		catch (SignException ex)
		{
			ess.showError(user, ex, signName);
			return false;
		}
	}

	public final boolean onSignBreak(final BlockBreakEvent event, final IEssentials ess)
	{
		final ISign sign = new BlockSign(event.getBlock());
		final User user = ess.getUser(event.getPlayer());
		try
		{
			return (user.isAuthorized("essentials.signs." + signName.toLowerCase() + ".break")
					|| user.isAuthorized("essentials.signs.break." + signName.toLowerCase()))
				   && onSignBreak(sign, user, getUsername(user), ess);
		}
		catch (SignException ex)
		{
			ess.showError(user, ex, signName);
			return false;
		}
	}

	protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		return true;
	}

	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		return true;
	}

	protected boolean onSignBreak(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		return true;
	}

	protected final void validateCharge(final ISign sign, final int index) throws SignException
	{
		final String line = sign.getLine(index);
		if (line.isEmpty())
		{
			return;
		}

		final boolean isMoney = line.matches("^[^0-9-][\\.0-9]+");
		if (isMoney)
		{
			final double quantity = Double.parseDouble(line.substring(1));
			if (quantity <= 0)
			{
				throw new SignException(Util.i18n("moreThanZero"));
			}
			sign.setLine(index, Util.formatCurrency(quantity));
		}
		else
		{
			final String[] split = line.split("[ :-]+", 2);
			if (split.length != 2)
			{
				throw new SignException(Util.i18n("invalidCharge"));
			}
			try
			{
				final int quantity = Integer.parseInt(split[0]);
				if (quantity <= 1)
				{
					throw new SignException(Util.i18n("moreThanZero"));
				}
				final String item = split[1].toLowerCase();
				if (!item.equalsIgnoreCase("times"))
				{
					getItemStack(item);
				}
				sign.setLine(index, quantity + " " + item);
			}
			catch (NumberFormatException ex)
			{
				throw new SignException(Util.i18n("invalidCharge"), ex);
			}
		}
	}

	protected final ItemStack getItemStack(final String itemName) throws SignException
	{
		try
		{
			return ItemDb.get(itemName);
		}
		catch (Exception ex)
		{
			throw new SignException(ex.getMessage(), ex);
		}
	}

	protected final Charge getCharge(final ISign sign, final int index, final IEssentials ess) throws SignException
	{
		final String line = sign.getLine(index);
		if (line.isEmpty())
		{
			return new Charge(signName.toLowerCase() + "sign", ess);
		}

		final boolean isMoney = line.matches("^[^0-9-][\\.0-9]+");
		if (isMoney)
		{
			final double quantity = Double.parseDouble(line.substring(1));
			if (quantity <= 0)
			{
				throw new SignException(Util.i18n("moreThanZero"));
			}
			return new Charge(quantity, ess);
		}
		else
		{
			final String[] split = line.split("[ :-]+", 2);
			if (split.length != 2)
			{
				throw new SignException(Util.i18n("invalidCharge"));
			}
			try
			{
				final int quantity = Integer.parseInt(split[0]);
				if (quantity <= 1)
				{
					throw new SignException(Util.i18n("moreThanZero"));
				}
				final String item = split[1].toLowerCase();
				if (item.equalsIgnoreCase("times"))
				{
					sign.setLine(index, (quantity - 1) + " times");
					return new Charge(signName.toLowerCase() + "sign", ess);
				}
				else
				{
					final ItemStack stack = getItemStack(item);
					stack.setAmount(quantity);
					return new Charge(quantity, ess);
				}
			}
			catch (NumberFormatException ex)
			{
				throw new SignException(Util.i18n("invalidCharge"), ex);
			}
		}
	}


	static class EventSign implements ISign
	{
		private final transient SignChangeEvent event;

		public EventSign(final SignChangeEvent event)
		{
			this.event = event;
		}

		public final String getLine(final int index)
		{
			return event.getLine(index);
		}

		public final void setLine(final int index, final String text)
		{
			event.setLine(index, text);
		}
	}


	static class BlockSign implements ISign
	{
		private final transient Sign sign;

		public BlockSign(final Block block)
		{
			this.sign = new CraftSign(block);
		}

		public final String getLine(final int index)
		{
			return sign.getLine(index);
		}

		public final void setLine(final int index, final String text)
		{
			sign.setLine(index, text);
		}
	}


	public interface ISign
	{
		String getLine(final int index);

		void setLine(final int index, final String text);
	}
}
