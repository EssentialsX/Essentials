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
		final Trade charge = getTrade(sign, 2, true, true, ess);
		charge.isAffordableFor(player);
		sign.setLine(3, "§8" + username);
		charge.charge(player);
		Trade.log("Sign", "Trade", "Create", username, charge, username, null, ess);
		return true;
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		if (sign.getLine(3).substring(2).equalsIgnoreCase(username))
		{
			final Trade stored = getTrade(sign, 1, true, true, ess);
			substractAmount(sign, 1, stored);
			stored.pay(player);
			Trade.log("Sign", "Trade", "OwnerInteract", username, null, username, stored, ess);
		}
		else
		{
			final Trade charge = getTrade(sign, 1, false, false, ess);
			final Trade trade = getTrade(sign, 2, false, true, ess);
			charge.isAffordableFor(player);
			substractAmount(sign, 2, trade);
			trade.pay(player);
			addAmount(sign, 1, charge);
			charge.charge(player);
			Trade.log("Sign", "Trade", "Interact", sign.getLine(3), charge, username, trade, ess);
		}
		sign.updateSign();
		return true;
	}

	@Override
	protected boolean onSignBreak(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		if ((sign.getLine(3).length() > 3 && sign.getLine(3).substring(2).equalsIgnoreCase(username))
			|| player.isAuthorized("essentials.signs.trade.override"))
		{
			final Trade stored1 = getTrade(sign, 1, true, false, ess);
			final Trade stored2 = getTrade(sign, 2, true, false, ess);
			stored1.pay(player);
			stored2.pay(player);
			Trade.log("Sign", "Trade", "Break", username, stored2, username, stored1, ess);
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
				if (Util.formatCurrency(money).length() * 2 > 15)
				{
					throw new SignException("Line can be too long!");
				}
				sign.setLine(index, Util.formatCurrency(money) + ":0");
				return;
			}
		}

		if (split.length == 2 && amountNeeded)
		{
			final Double money = getMoney(split[0]);
			final Double amount = getDoublePositive(split[1]);
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
			String newline = amount + " " + split[1] + ":0";
			if ((newline + amount).length() > 16)
			{
				throw new SignException("Line can be too long!");
			}
			sign.setLine(index, newline);
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

	protected final Trade getTrade(final ISign sign, final int index, final boolean fullAmount, final boolean notEmpty, final IEssentials ess) throws SignException
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
			final Double amount = notEmpty ? getDoublePositive(split[1]) : getDouble(split[1]);
			if (money != null && amount != null)
			{
				return new Trade(fullAmount ? amount : money, ess);
			}
		}

		if (split.length == 3)
		{
			final int stackamount = getIntegerPositive(split[0]);
			final ItemStack item = getItemStack(split[1], stackamount);
			int amount = getInteger(split[2]);
			amount -= amount % stackamount;
			if (notEmpty && (amount < 1 || stackamount < 1 || item.getTypeId() == 0))
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
