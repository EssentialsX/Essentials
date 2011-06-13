package com.earth2me.essentials.signs;

import com.earth2me.essentials.Trade;
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
		try
		{
			final boolean ret = onSignCreate(sign, user, getUsername(user), ess);
			if (ret)
			{
				sign.setLine(0, String.format(FORMAT_SUCCESS, this.signName));
			}
			return ret;
		}
		catch (ChargeException ex)
		{
			ess.showError(user, ex, signName);
		}
		catch (SignException ex)
		{
			ess.showError(user, ex, signName);
		}
		return false;
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

	protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
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

	protected final void validateTrade(final ISign sign, final int index, final IEssentials ess) throws SignException
	{
		final String line = sign.getLine(index).trim();
		if (line.isEmpty())
		{
			return;
		}
		final Trade trade = getTrade(sign, index, 0, ess);
		final Double money = trade.getMoney();
		if (money != null)
		{
			sign.setLine(index, Util.formatCurrency(money));
		}
	}

	

	protected final void validateTrade(final ISign sign, final int amountIndex, final int itemIndex,
									   final User player, final IEssentials ess) throws SignException
	{
		final Trade trade = getTrade(sign, amountIndex, itemIndex, player, ess);
		final ItemStack item = trade.getItemStack();
		sign.setLine(amountIndex, Integer.toString(item.getAmount()));
		sign.setLine(itemIndex, sign.getLine(itemIndex).trim());
	}

	protected final Trade getTrade(final ISign sign, final int amountIndex, final int itemIndex,
								   final User player, final IEssentials ess) throws SignException
	{

		final ItemStack item = getItemStack(sign.getLine(itemIndex), 1);
		final int amount = Math.min(getInteger(sign.getLine(amountIndex)), item.getType().getMaxStackSize() * player.getInventory().getSize());
		if (item.getTypeId() == 0 || amount < 1)
		{
			throw new SignException(Util.i18n("moreThanZero"));
		}
		item.setAmount(amount);
		return new Trade(item, ess);
	}

	protected final void validateInteger(final ISign sign, final int index) throws SignException
	{
		final String line = sign.getLine(index).trim();
		if (line.isEmpty())
		{
			throw new SignException("Empty line " + index);
		}
		final int quantity = getInteger(line);
		sign.setLine(index, Integer.toString(quantity));
	}

	protected final int getInteger(final String line) throws SignException
	{
		try
		{
			final int quantity = Integer.parseInt(line);
			if (quantity <= 1)
			{
				throw new SignException(Util.i18n("moreThanZero"));
			}
			return quantity;
		}
		catch (NumberFormatException ex)
		{
			throw new SignException("Invalid sign", ex);
		}
	}

	protected final ItemStack getItemStack(final String itemName, final int quantity) throws SignException
	{
		try
		{
			final ItemStack item = ItemDb.get(itemName);
			item.setAmount(quantity);
			return item;
		}
		catch (Exception ex)
		{
			throw new SignException(ex.getMessage(), ex);
		}
	}

	protected final Double getMoney(final String line) throws SignException
	{
		final boolean isMoney = line.matches("^[^0-9-\\.][\\.0-9]+");
		return isMoney ? getDouble(line.substring(1)) : null;
	}

	protected final Double getDouble(final String line) throws SignException
	{
		try
		{
			final double quantity = Double.parseDouble(line);
			if (quantity <= 0.0)
			{
				throw new SignException(Util.i18n("moreThanZero"));
			}
			return quantity;
		}
		catch (NumberFormatException ex)
		{
			throw new SignException(ex.getMessage(), ex);
		}
	}

	protected final Trade getTrade(final ISign sign, final int index, final IEssentials ess) throws SignException
	{
		return getTrade(sign, index, 1, ess);
	}

	protected final Trade getTrade(final ISign sign, final int index, final int decrement, final IEssentials ess) throws SignException
	{
		final String line = sign.getLine(index).trim();
		if (line.isEmpty())
		{
			return new Trade(signName.toLowerCase() + "sign", ess);
		}

		final Double money = getMoney(line);
		if (money == null)
		{
			final String[] split = line.split("[ :]+", 2);
			if (split.length != 2)
			{
				throw new SignException(Util.i18n("invalidCharge"));
			}
			final int quantity = getInteger(split[0]);

			final String item = split[1].toLowerCase();
			if (item.equalsIgnoreCase("times"))
			{
				sign.setLine(index, (quantity - decrement) + " times");
				return new Trade(signName.toLowerCase() + "sign", ess);
			}
			else
			{
				final ItemStack stack = getItemStack(item, quantity);
				sign.setLine(index, quantity + " " + item);
				return new Trade(quantity, ess);
			}
		}
		else
		{
			return new Trade(money, ess);
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
