package com.nijiko.coelho.iConomy.system;

import com.earth2me.essentials.api.Economy;
import com.nijiko.coelho.iConomy.existCheck;
import java.util.ArrayList;


public class Account
{
	private String name;

	public Holdings getHoldings()
	{
		return new Holdings(name, this.name);
	}

	public String getName()
	{
		return name;
	}

	/**
	 * Essentials does not support hidden accounts.
	 * @return false
	 */
	public boolean setHidden(boolean hidden)
	{
		return true;
	}

	//Simply set the account variable type?
	public Account(String name)
	{
		this.name = name;
	}

	public double getBalance()
	{
		if (!existCheck.exist(name))
		{
			if (Economy.accountExist(name))
			{
				return Economy.getMoney(name);
			}
			return 0;
		}
		return Economy.getMoney(name);
	}

	public void setBalance(double bal)
	{
		if (!existCheck.exist(name))
		{
			if (Economy.accountExist(name))
			{
				Economy.setMoney(name, bal);
			}
			return;
		}
		Economy.setMoney(name, bal);
	}

	public void add(double money)
	{
		if (!existCheck.exist(name))
		{
			if (Economy.accountExist(name))
			{
				Economy.add(name, money);
			}
			return;
		}
		Economy.add(name, money);
	}

	public void divide(double money)
	{
		if (!existCheck.exist(name))
		{
			if (Economy.accountExist(name))
			{
				Economy.divide(name, money);
			}
			return;
		}
		Economy.divide(name, money);
	}

	public ArrayList<Bank> withBanks()
	{
		ArrayList<Bank> banks = new ArrayList<Bank>();
		if (Economy.accountExist(name + "-bank"))
		{
			Bank bank = new Bank("EcoBanks");
			banks.add(bank);
			return banks;
		}
		return null;
	}

	public ArrayList<Bank> getBankAccounts()
	{
		ArrayList<Bank> banks = new ArrayList<Bank>();
		int breaker = 0;
		Bank bank;
		for (int ctr = 1; breaker != 1; ctr++)
		{
			if (ctr == 1)
			{
				if (Economy.accountExist(name + "-bank"))
				{
					bank = new Bank(name + "-bank");
					banks.add(bank);
				}
				else
				{
					breaker = 1;
				}
			}
			if (Economy.accountExist(name + "-bank" + Integer.toString(ctr)) && ctr != 1)
			{
				bank = new Bank(name + "-bank" + Integer.toString(ctr));
				banks.add(bank);
			}
			else
			{
				breaker = 1;
			}
		}
		return null;
	}

	public void multiply(double money)
	{
		if (!existCheck.exist(name))
		{
			if (Economy.accountExist(name))
			{
				Economy.multiply(name, money);
			}
			return;
		}
		Economy.multiply(name, money);
	}

	public void subtract(double money)
	{
		if (!existCheck.exist(name))
		{
			if (Economy.accountExist(name))
			{
				Economy.subtract(name, money);
			}
			return;
		}
		Economy.subtract(name, money);
	}

	public void resetBalance()
	{
		this.setBalance(0);
	}

	public boolean hasEnough(double amount)
	{
		return amount <= this.getBalance();
	}

	public boolean hasOver(double amount)
	{
		return amount < this.getBalance();
	}

	/**
	 * Essentials does not support hidden accounts.
	 * @return false
	 */
	public boolean isHidden()
	{
		return false;
	}

	public boolean isNegative()
	{
		return this.getBalance() < 0.0;
	}

	public Bank getMainBank()
	{
		Bank bank = null;
		if (!Economy.accountExist(name + "-bank"))
		{
			Economy.newAccount(name + "-bank");
			bank = new Bank(name + "-bank");
			return bank;
		}
		bank = new Bank(name + "-bank");
		return bank;


	}

	/**
	 * Because some plugins like to use depricated methods I must save 
	 * admins' log from the overflow of dumb
	 */
	@Deprecated
	public void save()
	{
	}
;
}
