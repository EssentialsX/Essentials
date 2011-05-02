package com.nijiko.coelho.iConomy.system;

import com.earth2me.essentials.api.Economy;
import com.nijiko.coelho.iConomy.existCheck;


public class Account
{
	private String name;

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
