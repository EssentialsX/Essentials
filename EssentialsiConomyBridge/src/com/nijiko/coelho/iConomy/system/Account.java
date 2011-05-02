package com.nijiko.coelho.iConomy.system;

import com.earth2me.essentials.EcoAPI;
import com.nijiko.coelho.iConomy.existCheck;


public class Account
{
	private String name;

	//Fake getname
	public String getName()
	{
		return name;
	}

	//Essentials doesnt have hidden accounts so just say yeah whatever!
	public boolean setHidden(boolean hidden)
	{
		return true;
	}

	//Simply set the account variable type?
	public Account(String name)
	{
		this.name = name;
	}

	//Fake return balance
	public double getBalance()
	{
		if (!existCheck.exist(name))
		{
			if (EcoAPI.accountExist(name))
			{
				return EcoAPI.getMoney(name);
			}
			return 0;
		}
		return EcoAPI.getMoney(name);
	}

	//Fake Set balance
	public void setBalance(double bal)
	{
		if (!existCheck.exist(name))
		{
			if (EcoAPI.accountExist(name))
			{
				EcoAPI.setMoney(name, bal);
			}
			return;
		}
		EcoAPI.setMoney(name, bal);
	}

	//Fake add balance
	public void add(double money)
	{
		if (!existCheck.exist(name))
		{
			if (EcoAPI.accountExist(name))
			{
				EcoAPI.add(name, money);
			}
			return;
		}
		EcoAPI.add(name, money);
	}

	//Fake divide balance
	public void divide(double money)
	{
		if (!existCheck.exist(name))
		{
			if (EcoAPI.accountExist(name))
			{
				EcoAPI.divide(name, money);
			}
			return;
		}
		EcoAPI.divide(name, money);
	}

	//Fake multiply balance
	public void multiply(double money)
	{
		if (!existCheck.exist(name))
		{
			if (EcoAPI.accountExist(name))
			{
				EcoAPI.multiply(name, money);
			}
			return;
		}
		EcoAPI.multiply(name, money);
	}

	//Fake subtract balance
	public void subtract(double money)
	{
		if (!existCheck.exist(name))
		{
			if (EcoAPI.accountExist(name))
			{
				EcoAPI.subtract(name, money);
			}
			return;
		}
		EcoAPI.subtract(name, money);
	}

	//fake reset balance!
	public void resetBalance()
	{
		this.setBalance(0);
	}

	//fake bal check
	public boolean hasEnough(double amount)
	{
		return amount <= this.getBalance();
	}

	//fake another balance check
	public boolean hasOver(double amount)
	{
		return amount < this.getBalance();
	}

	//Again we dont have hidden accounts here!
	public boolean isHidden()
	{
		return false;
	}

	//Fake is negative check!
	public boolean isNegative()
	{
		return this.getBalance() < 0.0;
	}

	//Because some plugins like to use depricated methods I must save
	//admins' log from the overflow of dumb
	public void save()
	{
	}
;
}
