package com.earth2me.essentials.register.payment.methods;

import com.earth2me.essentials.register.payment.Method;
import cosine.boseconomy.BOSEconomy;
import org.bukkit.plugin.Plugin;


/**
 * BOSEconomy 6 Implementation of Method
 *
 * @author Nijikokun <nijikokun@shortmail.com> (@nijikokun)
 * @copyright (c) 2011
 * @license AOL license <http://aol.nexua.org>
 */
@SuppressWarnings("deprecation")
public class BOSE6 implements Method
{
	private BOSEconomy BOSEconomy;

	@Override
	public BOSEconomy getPlugin()
	{
		return this.BOSEconomy;
	}

	@Override
	public String getName()
	{
		return "BOSEconomy";
	}
	
	@Override
	public String getLongName()
	{
		return  getName();
	}

	@Override
	public String getVersion()
	{
		return "0.6.2";
	}

	@Override
	public int fractionalDigits()
	{
		return 0;
	}

	@Override
	public String format(double amount)
	{
		String currency = this.BOSEconomy.getMoneyNamePlural();

		if (amount == 1)
		{
			currency = this.BOSEconomy.getMoneyName();
		}

		return amount + " " + currency;
	}

	@Override
	public boolean hasBanks()
	{
		return true;
	}

	@Override
	public boolean hasBank(String bank)
	{
		return this.BOSEconomy.bankExists(bank);
	}

	@Override
	public boolean hasAccount(String name)
	{
		return this.BOSEconomy.playerRegistered(name, false);
	}

	@Override
	public boolean hasBankAccount(String bank, String name)
	{
		return this.BOSEconomy.isBankOwner(bank, name)
			   || this.BOSEconomy.isBankMember(bank, name);
	}

	@Override
	public boolean createAccount(String name)
	{
		if (hasAccount(name))
		{
			return false;
		}

		this.BOSEconomy.registerPlayer(name);
		return true;
	}

	@Override
	public boolean createAccount(String name, Double balance)
	{
		if (hasAccount(name))
		{
			return false;
		}

		this.BOSEconomy.registerPlayer(name);
		this.BOSEconomy.setPlayerMoney(name, balance, false);
		return true;
	}

	@Override
	public MethodAccount getAccount(String name)
	{
		if (!hasAccount(name))
		{
			return null;
		}

		return new BOSEAccount(name, this.BOSEconomy);
	}

	@Override
	public MethodBankAccount getBankAccount(String bank, String name)
	{
		if (!hasBankAccount(bank, name))
		{
			return null;
		}

		return new BOSEBankAccount(bank, BOSEconomy);
	}

	@Override
	public boolean isCompatible(Plugin plugin)
	{
		return plugin.getDescription().getName().equalsIgnoreCase("boseconomy")
			   && plugin instanceof BOSEconomy
			   && plugin.getDescription().getVersion().equals("0.6.2");
	}

	@Override
	public void setPlugin(Plugin plugin)
	{
		BOSEconomy = (BOSEconomy)plugin;
	}


	public class BOSEAccount implements MethodAccount
	{
		private final String name;
		private final BOSEconomy BOSEconomy;

		public BOSEAccount(String name, BOSEconomy bOSEconomy)
		{
			this.name = name;
			this.BOSEconomy = bOSEconomy;
		}

		@Override
		public double balance()
		{
			return (double)this.BOSEconomy.getPlayerMoney(this.name);
		}

		@Override
		public boolean set(double amount)
		{
			int IntAmount = (int)Math.ceil(amount);
			return this.BOSEconomy.setPlayerMoney(this.name, IntAmount, false);
		}

		@Override
		public boolean add(double amount)
		{
			int IntAmount = (int)Math.ceil(amount);
			return this.BOSEconomy.addPlayerMoney(this.name, IntAmount, false);
		}

		@Override
		public boolean subtract(double amount)
		{
			int IntAmount = (int)Math.ceil(amount);
			int balance = (int)this.balance();
			return this.BOSEconomy.setPlayerMoney(this.name, (balance - IntAmount), false);
		}

		@Override
		public boolean multiply(double amount)
		{
			int IntAmount = (int)Math.ceil(amount);
			int balance = (int)this.balance();
			return this.BOSEconomy.setPlayerMoney(this.name, (balance * IntAmount), false);
		}

		@Override
		public boolean divide(double amount)
		{
			int IntAmount = (int)Math.ceil(amount);
			int balance = (int)this.balance();
			return this.BOSEconomy.setPlayerMoney(this.name, (balance / IntAmount), false);
		}

		@Override
		public boolean hasEnough(double amount)
		{
			return (this.balance() >= amount);
		}

		@Override
		public boolean hasOver(double amount)
		{
			return (this.balance() > amount);
		}

		@Override
		public boolean hasUnder(double amount)
		{
			return (this.balance() < amount);
		}

		@Override
		public boolean isNegative()
		{
			return (this.balance() < 0);
		}

		@Override
		public boolean remove()
		{
			return false;
		}
	}


	public class BOSEBankAccount implements MethodBankAccount
	{
		private final String bank;
		private final BOSEconomy BOSEconomy;

		public BOSEBankAccount(String bank, BOSEconomy bOSEconomy)
		{
			this.bank = bank;
			this.BOSEconomy = bOSEconomy;
		}

		@Override
		public String getBankName()
		{
			return this.bank;
		}

		@Override
		public int getBankId()
		{
			return -1;
		}

		@Override
		public double balance()
		{
			return (double)this.BOSEconomy.getBankMoney(bank);
		}

		@Override
		public boolean set(double amount)
		{
			int IntAmount = (int)Math.ceil(amount);
			return this.BOSEconomy.setBankMoney(bank, IntAmount, true);
		}

		@Override
		public boolean add(double amount)
		{
			int IntAmount = (int)Math.ceil(amount);
			int balance = (int)this.balance();
			return this.BOSEconomy.setBankMoney(bank, (balance + IntAmount), false);
		}

		@Override
		public boolean subtract(double amount)
		{
			int IntAmount = (int)Math.ceil(amount);
			int balance = (int)this.balance();
			return this.BOSEconomy.setBankMoney(bank, (balance - IntAmount), false);
		}

		@Override
		public boolean multiply(double amount)
		{
			int IntAmount = (int)Math.ceil(amount);
			int balance = (int)this.balance();
			return this.BOSEconomy.setBankMoney(bank, (balance * IntAmount), false);
		}

		@Override
		public boolean divide(double amount)
		{
			int IntAmount = (int)Math.ceil(amount);
			int balance = (int)this.balance();
			return this.BOSEconomy.setBankMoney(bank, (balance / IntAmount), false);
		}

		@Override
		public boolean hasEnough(double amount)
		{
			return (this.balance() >= amount);
		}

		@Override
		public boolean hasOver(double amount)
		{
			return (this.balance() > amount);
		}

		@Override
		public boolean hasUnder(double amount)
		{
			return (this.balance() < amount);
		}

		@Override
		public boolean isNegative()
		{
			return (this.balance() < 0);
		}

		@Override
		public boolean remove()
		{
			return this.BOSEconomy.removeBank(bank);
		}
	}
}