package com.earth2me.essentials.register.payment.methods;

import com.earth2me.essentials.register.payment.Method;
import me.ashtheking.currency.Currency;
import me.ashtheking.currency.CurrencyList;
import org.bukkit.plugin.Plugin;


/**
 * MultiCurrency Method implementation.
 *
 * @author Acrobot @copyright (c) 2011 @license AOL license <http://aol.nexua.org>
 */
public class MCUR implements Method
{
	private Currency currencyList;

	@Override
	public Object getPlugin()
	{
		return this.currencyList;
	}

	@Override
	public String getName()
	{
		return "MultiCurrency";
	}

	@Override
	public String getLongName()
	{
		return getName();
	}

	@Override
	public String getVersion()
	{
		return "0.09";
	}

	@Override
	public int fractionalDigits()
	{
		return -1;
	}

	@Override
	public String format(double amount)
	{
		return amount + " Currency";
	}

	@Override
	public boolean hasBanks()
	{
		return false;
	}

	@Override
	public boolean hasBank(String bank)
	{
		return false;
	}

	@Override
	public boolean hasAccount(String name)
	{
		return true;
	}

	@Override
	public boolean hasBankAccount(String bank, String name)
	{
		return false;
	}

	@Override
	public boolean createAccount(String name)
	{
		CurrencyList.setValue((String)CurrencyList.maxCurrency(name)[0], name, 0);
		return true;
	}

	@Override
	public boolean createAccount(String name, Double balance)
	{
		CurrencyList.setValue((String)CurrencyList.maxCurrency(name)[0], name, balance);
		return true;
	}

	@Override
	public MethodAccount getAccount(String name)
	{
		return new MCurrencyAccount(name);
	}

	@Override
	public MethodBankAccount getBankAccount(String bank, String name)
	{
		return null;
	}

	@Override
	public boolean isCompatible(Plugin plugin)
	{
		return (plugin.getDescription().getName().equalsIgnoreCase("Currency")
				|| plugin.getDescription().getName().equalsIgnoreCase("MultiCurrency"))
			   && plugin instanceof Currency;
	}

	@Override
	public void setPlugin(Plugin plugin)
	{
		currencyList = (Currency)plugin;
	}


	public class MCurrencyAccount implements MethodAccount
	{
		private String name;

		public MCurrencyAccount(String name)
		{
			this.name = name;
		}

		@Override
		public double balance()
		{
			return CurrencyList.getValue((String)CurrencyList.maxCurrency(name)[0], name);
		}

		@Override
		public boolean set(double amount)
		{
			CurrencyList.setValue((String)CurrencyList.maxCurrency(name)[0], name, amount);
			return true;
		}

		@Override
		public boolean add(double amount)
		{
			return CurrencyList.add(name, amount);
		}

		@Override
		public boolean subtract(double amount)
		{
			return CurrencyList.subtract(name, amount);
		}

		@Override
		public boolean multiply(double amount)
		{
			return CurrencyList.multiply(name, amount);
		}

		@Override
		public boolean divide(double amount)
		{
			return CurrencyList.divide(name, amount);
		}

		@Override
		public boolean hasEnough(double amount)
		{
			return CurrencyList.hasEnough(name, amount);
		}

		@Override
		public boolean hasOver(double amount)
		{
			return CurrencyList.hasOver(name, amount);
		}

		@Override
		public boolean hasUnder(double amount)
		{
			return CurrencyList.hasUnder(name, amount);
		}

		@Override
		public boolean isNegative()
		{
			return CurrencyList.isNegative(name);
		}

		@Override
		public boolean remove()
		{
			return CurrencyList.remove(name);
		}
	}
}
