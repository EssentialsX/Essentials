package com.earth2me.essentials;

import com.earth2me.essentials.api.*;
import com.earth2me.essentials.perm.Permissions;
import com.earth2me.essentials.settings.MoneyHolder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.ServicePriority;


public class Economy implements IEconomy
{
	private final IEssentials ess;
	private final MoneyHolder npcs;

	public Economy(IEssentials ess)
	{
		this.ess = ess;
		this.npcs = new MoneyHolder(ess);
	}

	private double getNPCBalance(String name) throws UserDoesNotExistException
	{
		npcs.acquireReadLock();
		try
		{
			Map<String, Double> balances = npcs.getData().getBalances();
			if (balances == null)
			{
				throw new UserDoesNotExistException(name);
			}
			Double balance = npcs.getData().getBalances().get(name.toLowerCase(Locale.ENGLISH));
			if (balance == null)
			{
				throw new UserDoesNotExistException(name);
			}
			return balance;
		}
		finally
		{
			npcs.unlock();
		}
	}

	private void setNPCBalance(String name, double balance, boolean checkExistance) throws UserDoesNotExistException
	{
		npcs.acquireWriteLock();
		try
		{
			Map<String, Double> balances = npcs.getData().getBalances();
			if (balances == null)
			{
				balances = new HashMap<String, Double>();
				npcs.getData().setBalances(balances);
			}
			if (checkExistance && !balances.containsKey(name.toLowerCase(Locale.ENGLISH)))
			{
				throw new UserDoesNotExistException(name);
			}
			balances.put(name.toLowerCase(Locale.ENGLISH), balance);
		}
		finally
		{
			npcs.unlock();
		}
	}

	private double getStartingBalance()
	{
		double startingBalance = 0;
		ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		try
		{
			startingBalance = settings.getData().getEconomy().getStartingBalance();
		}
		finally
		{
			settings.unlock();
		}
		return startingBalance;
	}

	@Override
	public void onReload()
	{
		this.npcs.onReload(false);
	}

	@Override
	public double getMoney(String name) throws UserDoesNotExistException
	{
		IUser user = ess.getUser(name);
		if (user == null)
		{
			return getNPCBalance(name);
		}
		return user.getMoney();
	}

	@Override
	public void setMoney(String name, double balance) throws NoLoanPermittedException, UserDoesNotExistException
	{
		IUser user = ess.getUser(name);
		if (user == null)
		{
			setNPCBalance(name, balance, true);
			return;
		}
		if (balance < 0.0 && !Permissions.ECO_LOAN.isAuthorized(user))
		{
			throw new NoLoanPermittedException();
		}
		user.setMoney(balance);
	}

	@Override
	public void resetBalance(String name) throws NoLoanPermittedException, UserDoesNotExistException
	{
		setMoney(name, getStartingBalance());
	}

	@Override
	public String format(double amount)
	{
		return Util.formatCurrency(amount, ess);
	}

	@Override
	public boolean playerExists(String name)
	{
		try
		{
			getMoney(name);
			return true;
		}
		catch (UserDoesNotExistException ex)
		{
			return false;
		}
	}

	@Override
	public boolean isNPC(String name) throws UserDoesNotExistException
	{
		boolean result = ess.getUser(name) == null;
		if (result)
		{
			getNPCBalance(name);
		}
		return result;
	}

	@Override
	public boolean createNPC(String name)
	{
		try
		{
			if (isNPC(name))
			{

				setNPCBalance(name, getStartingBalance(), false);
				return true;
			}
		}
		catch (UserDoesNotExistException ex)
		{
			try
			{
				setNPCBalance(name, getStartingBalance(), false);
				return true;
			}
			catch (UserDoesNotExistException ex1)
			{
				//This should never happen!
			}
		}
		return false;
	}

	@Override
	public void removeNPC(String name) throws UserDoesNotExistException
	{
		npcs.acquireWriteLock();
		try
		{
			Map<String, Double> balances = npcs.getData().getBalances();
			if (balances == null)
			{
				balances = new HashMap<String, Double>();
				npcs.getData().setBalances(balances);
			}
			if (balances.containsKey(name.toLowerCase(Locale.ENGLISH)))
			{
				balances.remove(name.toLowerCase(Locale.ENGLISH));
			}
			else
			{
				throw new UserDoesNotExistException(name);
			}
		}
		finally
		{
			npcs.unlock();
		}
	}
}
