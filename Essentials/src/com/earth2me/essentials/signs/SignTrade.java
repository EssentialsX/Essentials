package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.inventory.ItemStack;


public class SignTrade extends EssentialsSign
{
	public SignTrade()
	{
		super("Trade");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		validateTrade(sign, 1, false, ess);
		validateTrade(sign, 2, true, ess);
		final Trade charge = getTrade(sign, 2, true, ess);
		charge.isAffordableFor(player);
		sign.setLine(3, "§8" + username);
		charge.charge(player);
		return true;
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		if (sign.getLine(3).substring(2).equalsIgnoreCase(username))
		{
			final Trade stored = getTrade(sign, 1, true, ess);
			substractAmount(sign, 1, stored);
			stored.pay(player);
		}
		else
		{
			final Trade charge = getTrade(sign, 1, false, ess);
			final Trade trade = getTrade(sign, 2, false, ess);
			charge.isAffordableFor(player);
			substractAmount(sign, 2, trade);
			trade.pay(player);
			addAmount(sign, 1, charge);
			charge.charge(player);
		}
		return true;
	}

	@Override
	protected boolean onSignBreak(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		if (sign.getLine(3).length() > 3 && sign.getLine(3).substring(2).equalsIgnoreCase(username))
		{
			final Trade stored1 = getTrade(sign, 1, true, ess);
			final Trade stored2 = getTrade(sign, 2, true, ess);
			stored1.pay(player);
			stored2.pay(player);
			return true;
		}
		else
		{
			return false;
		}
	}

	protected final void validateTrade(final ISign sign, final int index, final boolean amountNeeded, final IEssentials ess) throws SignException
	{
		final String line = sign.getLine(index).trim();
		if (line.isEmpty())
		{
			throw new SignException("Empty line");
		}
		final String[] split = line.split("[ :]+");

		if (split.length == 1 && !amountNeeded)
		{
			final Double money = getMoney(split[0]);
			if (money != null)
			{
				sign.setLine(index, Util.formatCurrency(money) + ":0");
				return;
			}
		}

		if (split.length == 2 && amountNeeded)
		{
			final Double money = getMoney(split[0]);
			final Double amount = getDouble(split[1]);
			if (money != null && amount != null)
			{
				sign.setLine(index, Util.formatCurrency(money) + ":" + Util.formatCurrency(amount).substring(1));
				return;
			}
		}

		if (split.length == 2 && !amountNeeded)
		{
			final int amount = getIntegerPositive(split[0]);
			final ItemStack item = getItemStack(split[1], amount);
			if (amount < 1 || item.getTypeId() == 0)
			{
				throw new SignException(Util.i18n("moreThanZero"));
			}
			sign.setLine(index, amount + " " + split[1] + ":0");
			return;
		}

		if (split.length == 3 && amountNeeded)
		{
			final int stackamount = getIntegerPositive(split[0]);
			final ItemStack item = getItemStack(split[1], stackamount);
			int amount = getIntegerPositive(split[2]);
			amount -= amount % stackamount;
			if (amount < 1 || stackamount < 1 || item.getTypeId() == 0)
			{
				throw new SignException(Util.i18n("moreThanZero"));
			}
			sign.setLine(index, stackamount + " " + split[1] + ":" + amount);
			return;
		}
		throw new SignException(Util.format("invalidSignLine", index));
	}

	protected final Trade getTrade(final ISign sign, final int index, final boolean fullAmount, final IEssentials ess) throws SignException
	{
		final String line = sign.getLine(index).trim();
		if (line.isEmpty())
		{
			throw new SignException("Empty line");
		}
		final String[] split = line.split("[ :]+");

		if (split.length == 2)
		{
			final Double money = getMoney(split[0]);
			final Double amount = getDouble(split[1]);
			if (money != null && amount != null)
			{
				return new Trade(fullAmount ? amount : money, ess);
			}
		}

		if (split.length == 3)
		{
			final int stackamount = getIntegerPositive(split[0]);
			final ItemStack item = getItemStack(split[1], stackamount);
			int amount = getIntegerPositive(split[2]);
			amount -= amount % stackamount;
			if (amount < 1 || stackamount < 1 || item.getTypeId() == 0)
			{
				throw new SignException(Util.i18n("moreThanZero"));
			}
			item.setAmount(fullAmount ? amount : stackamount);
			return new Trade(item, ess);
		}
		throw new SignException(Util.format("invalidSignLine", index));
	}

	protected final void substractAmount(final ISign sign, final int index, final Trade trade) throws SignException
	{
		final Double money = trade.getMoney();
		if (money != null)
		{
			changeAmount(sign, index, -money);
		}
		final ItemStack item = trade.getItemStack();
		if (item != null)
		{
			changeAmount(sign, index, -item.getAmount());
		}
	}

	protected final void addAmount(final ISign sign, final int index, final Trade trade) throws SignException
	{
		final Double money = trade.getMoney();
		if (money != null)
		{
			changeAmount(sign, index, money);
		}
		final ItemStack item = trade.getItemStack();
		if (item != null)
		{
			changeAmount(sign, index, item.getAmount());
		}
	}

	private void changeAmount(final ISign sign, final int index, final double value) throws SignException
	{
		final String line = sign.getLine(index).trim();
		if (line.isEmpty())
		{
			throw new SignException("Empty line");
		}
		final String[] split = line.split("[ :]+");

		if (split.length == 2)
		{
			final Double money = getMoney(split[0]);
			final Double amount = getDouble(split[1]);
			if (money != null && amount != null)
			{
				sign.setLine(index, Util.formatCurrency(money) + ":" + Util.formatCurrency(amount + value).substring(1));
				return;
			}
		}

		if (split.length == 3)
		{
			final int stackamount = getIntegerPositive(split[0]);
			final ItemStack item = getItemStack(split[1], stackamount);
			int amount = getInteger(split[2]);
			sign.setLine(index, stackamount + " " + split[1] + ":" + (amount + Math.round(value)));
			return;
		}
		throw new SignException(Util.format("invalidSignLine", index));
	}
}
