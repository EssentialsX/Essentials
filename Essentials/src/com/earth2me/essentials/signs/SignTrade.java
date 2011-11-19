package com.earth2me.essentials.signs;

import com.earth2me.essentials.*;
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
		Trade.log("Sign", "Trade", "Create", username, charge, username, null, sign.getBlock().getLocation(), ess);
		return true;
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		if (sign.getLine(3).substring(2).equalsIgnoreCase(username))
		{
			final Trade store = rechargeSign(sign, ess, player);
			Trade stored = null;
			try
			{
				stored = getTrade(sign, 1, true, true, ess);
				substractAmount(sign, 1, stored, ess);
				stored.pay(player);
			}
			catch (SignException e)
			{
				if (store == null)
				{
					throw new SignException(Util.i18n("tradeSignEmptyOwner"), e);
				}
			}
			Trade.log("Sign", "Trade", "OwnerInteract", username, store, username, stored, sign.getBlock().getLocation(), ess);
		}
		else
		{
			final Trade charge = getTrade(sign, 1, false, false, ess);
			final Trade trade = getTrade(sign, 2, false, true, ess);
			charge.isAffordableFor(player);
			if (!trade.pay(player, false))
			{
				throw new ChargeException("Full inventory");
			}
			substractAmount(sign, 2, trade, ess);
			addAmount(sign, 1, charge, ess);
			charge.charge(player);
			Trade.log("Sign", "Trade", "Interact", sign.getLine(3), charge, username, trade, sign.getBlock().getLocation(), ess);
		}
		sign.updateSign();
		return true;
	}

	private Trade rechargeSign(final ISign sign, final IEssentials ess, final User player) throws SignException, ChargeException
	{
		final Trade trade = getTrade(sign, 2, false, false, ess);
		if (trade.getItemStack() != null && player.getItemInHand() != null
			&& trade.getItemStack().getTypeId() == player.getItemInHand().getTypeId()
			&& trade.getItemStack().getDurability() == player.getItemInHand().getDurability())
		{
			int amount = player.getItemInHand().getAmount();
			amount -= amount % trade.getItemStack().getAmount();
			if (amount > 0)
			{
				final Trade store = new Trade(new ItemStack(player.getItemInHand().getTypeId(), amount, player.getItemInHand().getDurability()), ess);
				addAmount(sign, 2, store, ess);
				store.charge(player);
				return store;
			}
		}
		return null;
	}

	@Override
	protected boolean onSignBreak(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		if ((sign.getLine(3).length() > 3 && sign.getLine(3).substring(2).equalsIgnoreCase(username))
			|| player.isAuthorized("essentials.signs.trade.override"))
		{
			try
			{
				final Trade stored1 = getTrade(sign, 1, true, false, ess);
				final Trade stored2 = getTrade(sign, 2, true, false, ess);
				stored1.pay(player);
				stored2.pay(player);
				Trade.log("Sign", "Trade", "Break", username, stored2, username, stored1, sign.getBlock().getLocation(), ess);
			}
			catch (SignException e)
			{
				if (player.isAuthorized("essentials.signs.trade.override"))
				{
					return true;
				}
				throw e;
			}
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
				if (Util.formatCurrency(money, ess).length() * 2 > 15)
				{
					throw new SignException("Line can be too long!");
				}
				sign.setLine(index, Util.formatCurrency(money, ess) + ":0");
				return;
			}
		}

		if (split.length == 2 && amountNeeded)
		{
			final Double money = getMoney(split[0]);
			Double amount = getDoublePositive(split[1]);
			if (money != null && amount != null)
			{
				amount -= amount % money;
				if (amount < 0.01 || money < 0.01)
				{
					throw new SignException(Util.i18n("moreThanZero"));
				}
				sign.setLine(index, Util.formatCurrency(money, ess) + ":" + Util.formatCurrency(amount, ess).substring(1));
				return;
			}
		}

		if (split.length == 2 && !amountNeeded)
		{
			final int amount = getIntegerPositive(split[0]);
			final ItemStack item = getItemStack(split[1], amount, ess);
			if (amount < 1 || item.getTypeId() == 0)
			{
				throw new SignException(Util.i18n("moreThanZero"));
			}
			String newline = amount + " " + split[1] + ":0";
			if ((newline + amount).length() > 15)
			{
				throw new SignException("Line can be too long!");
			}
			sign.setLine(index, newline);
			return;
		}

		if (split.length == 3 && amountNeeded)
		{
			final int stackamount = getIntegerPositive(split[0]);
			final ItemStack item = getItemStack(split[1], stackamount, ess);
			int amount = getIntegerPositive(split[2]);
			amount -= amount % stackamount;
			if (amount < 1 || stackamount < 1 || item.getTypeId() == 0)
			{
				throw new SignException(Util.i18n("moreThanZero"));
			}
			sign.setLine(index, stackamount + " " + split[1] + ":" + amount);
			return;
		}
		throw new SignException(Util.format("invalidSignLine", index + 1));
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
			try
			{
				final Double money = getMoney(split[0]);
				final Double amount = notEmpty ? getDoublePositive(split[1]) : getDouble(split[1]);
				if (money != null && amount != null)
				{
					return new Trade(fullAmount ? amount : money, ess);
				}
			}
			catch (SignException e)
			{
				throw new SignException(Util.i18n("tradeSignEmpty"));
			}
		}

		if (split.length == 3)
		{
			final int stackamount = getIntegerPositive(split[0]);
			final ItemStack item = getItemStack(split[1], stackamount, ess);
			int amount = getInteger(split[2]);
			amount -= amount % stackamount;
			if (notEmpty && (amount < 1 || stackamount < 1 || item.getTypeId() == 0))
			{
				throw new SignException(Util.i18n("tradeSignEmpty"));
			}
			item.setAmount(fullAmount ? amount : stackamount);
			return new Trade(item, ess);
		}
		throw new SignException(Util.format("invalidSignLine", index + 1));
	}

	protected final void substractAmount(final ISign sign, final int index, final Trade trade, final IEssentials ess) throws SignException
	{
		final Double money = trade.getMoney();
		if (money != null)
		{
			changeAmount(sign, index, -money, ess);
		}
		final ItemStack item = trade.getItemStack();
		if (item != null)
		{
			changeAmount(sign, index, -item.getAmount(), ess);
		}
	}

	protected final void addAmount(final ISign sign, final int index, final Trade trade, final IEssentials ess) throws SignException
	{
		final Double money = trade.getMoney();
		if (money != null)
		{
			changeAmount(sign, index, money, ess);
		}
		final ItemStack item = trade.getItemStack();
		if (item != null)
		{
			changeAmount(sign, index, item.getAmount(), ess);
		}
	}

	private void changeAmount(final ISign sign, final int index, final double value, final IEssentials ess) throws SignException
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
				final String newline = Util.formatCurrency(money, ess) + ":" + Util.formatCurrency(amount + value, ess).substring(1);
				if (newline.length() > 15)
				{
					throw new SignException("Line too long!");
				}
				sign.setLine(index, newline);
				return;
			}
		}

		if (split.length == 3)
		{
			final int stackamount = getIntegerPositive(split[0]);
			//TODO: Unused local variable
			final ItemStack item = getItemStack(split[1], stackamount, ess);
			final int amount = getInteger(split[2]);
			final String newline = stackamount + " " + split[1] + ":" + (amount + Math.round(value));
			if (newline.length() > 15)
			{
				throw new SignException("Line too long!");
			}
			sign.setLine(index, newline);
			return;
		}
		throw new SignException(Util.format("invalidSignLine", index + 1));
	}
}
