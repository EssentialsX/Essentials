package com.earth2me.essentials.register.payment.methods;

import com.earth2me.essentials.register.payment.Method;
import com.iConomy.iConomy;
import com.iConomy.system.Account;
import com.iConomy.system.BankAccount;
import com.iConomy.system.Holdings;
import com.iConomy.util.Constants;


import org.bukkit.plugin.Plugin;


/**
 * iConomy 5 Implementation of Method
 *
 * @author Nijikokun <nijikokun@shortmail.com> (@nijikokun)
 * @copyright (c) 2011
 * @license AOL license <http://aol.nexua.org>
 */
public class iCo5 implements Method
{
	private iConomy iConomy;

	public iConomy getPlugin()
	{
		return this.iConomy;
	}

	public String getName()
	{
		return "iConomy";
	}

	public String getVersion()
	{
		return "5";
	}

	public int fractionalDigits()
	{
		return 2;
	}

	public String format(double amount)
	{
		return com.iConomy.iConomy.format(amount);
	}

	public boolean hasBanks()
	{
		return Constants.Banking;
	}

	public boolean hasBank(String bank)
	{
		return (hasBanks()) && com.iConomy.iConomy.Banks.exists(bank);
	}

	public boolean hasAccount(String name)
	{
		return com.iConomy.iConomy.hasAccount(name);
	}

	public boolean hasBankAccount(String bank, String name)
	{
		return (hasBank(bank)) && com.iConomy.iConomy.getBank(bank).hasAccount(name);
	}

	public MethodAccount getAccount(String name)
	{
		return new iCoAccount(com.iConomy.iConomy.getAccount(name));
	}

	public MethodBankAccount getBankAccount(String bank, String name)
	{
		return new iCoBankAccount(com.iConomy.iConomy.getBank(bank).getAccount(name));
	}

	public boolean isCompatible(Plugin plugin)
	{
		return plugin.getDescription().getName().equalsIgnoreCase("iconomy")
			   && plugin.getClass().getName().equals("com.iConomy.iConomy")
			   && plugin instanceof iConomy;
	}

	public void setPlugin(Plugin plugin)
	{
		iConomy = (iConomy)plugin;
	}


	public class iCoAccount implements MethodAccount
	{
		private Account account;
		private Holdings holdings;

		public iCoAccount(Account account)
		{
			this.account = account;
			this.holdings = account.getHoldings();
		}

		public Account getiCoAccount()
		{
			return account;
		}

		public double balance()
		{
			return this.holdings.balance();
		}

		public boolean set(double amount)
		{
			if (this.holdings == null)
			{
				return false;
			}
			this.holdings.set(amount);
			return true;
		}

		public boolean add(double amount)
		{
			if (this.holdings == null)
			{
				return false;
			}
			this.holdings.add(amount);
			return true;
		}

		public boolean subtract(double amount)
		{
			if (this.holdings == null)
			{
				return false;
			}
			this.holdings.subtract(amount);
			return true;
		}

		public boolean multiply(double amount)
		{
			if (this.holdings == null)
			{
				return false;
			}
			this.holdings.multiply(amount);
			return true;
		}

		public boolean divide(double amount)
		{
			if (this.holdings == null)
			{
				return false;
			}
			this.holdings.divide(amount);
			return true;
		}

		public boolean hasEnough(double amount)
		{
			return this.holdings.hasEnough(amount);
		}

		public boolean hasOver(double amount)
		{
			return this.holdings.hasOver(amount);
		}

		public boolean hasUnder(double amount)
		{
			return this.holdings.hasUnder(amount);
		}

		public boolean isNegative()
		{
			return this.holdings.isNegative();
		}

		public boolean remove()
		{
			if (this.account == null)
			{
				return false;
			}
			this.account.remove();
			return true;
		}
	}


	public class iCoBankAccount implements MethodBankAccount
	{
		private BankAccount account;
		private Holdings holdings;

		public iCoBankAccount(BankAccount account)
		{
			this.account = account;
			this.holdings = account.getHoldings();
		}

		public BankAccount getiCoBankAccount()
		{
			return account;
		}

		public String getBankName()
		{
			return this.account.getBankName();
		}

		public int getBankId()
		{
			return this.account.getBankId();
		}

		public double balance()
		{
			return this.holdings.balance();
		}

		public boolean set(double amount)
		{
			if (this.holdings == null)
			{
				return false;
			}
			this.holdings.set(amount);
			return true;
		}

		public boolean add(double amount)
		{
			if (this.holdings == null)
			{
				return false;
			}
			this.holdings.add(amount);
			return true;
		}

		public boolean subtract(double amount)
		{
			if (this.holdings == null)
			{
				return false;
			}
			this.holdings.subtract(amount);
			return true;
		}

		public boolean multiply(double amount)
		{
			if (this.holdings == null)
			{
				return false;
			}
			this.holdings.multiply(amount);
			return true;
		}

		public boolean divide(double amount)
		{
			if (this.holdings == null)
			{
				return false;
			}
			this.holdings.divide(amount);
			return true;
		}

		public boolean hasEnough(double amount)
		{
			return this.holdings.hasEnough(amount);
		}

		public boolean hasOver(double amount)
		{
			return this.holdings.hasOver(amount);
		}

		public boolean hasUnder(double amount)
		{
			return this.holdings.hasUnder(amount);
		}

		public boolean isNegative()
		{
			return this.holdings.isNegative();
		}

		public boolean remove()
		{
			if (this.account == null)
			{
				return false;
			}
			this.account.remove();
			return true;
		}
	}
}