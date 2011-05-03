package com.iConomy.system;

import com.earth2me.essentials.api.Economy;
import com.iConomy.existCheck;


public class Holdings
{
	private String name = "";
	private boolean bank = false;
	private String bankId = null;

	public Holdings(String name)
	{
		this.name = name;
	}

	public Holdings(String id, String name)
	{
		this.bankId = id;
		this.name = name;
	}

	public Holdings(String id, String name, boolean bank)
	{
		this.bank = bank;
		this.bankId = id;
		this.name = name;
	}

	public boolean isBank()
	{
		return bank;
	}

	public double balance()
	{
		return get();
	}

	public double get()
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

	public void set(double bal)
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

	public void reset()
	{
		this.set(0);
	}

	public boolean hasEnough(double amount)
	{
		return amount <= this.get();
	}

	public boolean hasOver(double amount)
	{
		return amount < this.get();
	}

	public boolean isNegative()
	{
		return this.get() < 0.0;
	}
}
