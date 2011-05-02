package com.nijiko.coelho.iConomy.system;

import com.earth2me.essentials.api.Economy;
import com.nijiko.coelho.iConomy.existCheck;


public class Bank
{
	private String id = null;
	private String name = null;
	
	public Bank(String name)
	{
		this.id = name;
		this.name = name;
	}
	//The fake formatter

	public String format(double amount)
	{
		return Economy.format(amount);
	}

	//Fake currency!
	public String getCurrency()
	{
		return Economy.getCurrency();
	}

	//Fake "does player have an account?" but essentials eco doesnt need to make one, so TRUE, unless its an NPC.
	public boolean hasAccount(String account)
	{
		if (!existCheck.exist(account))
		{
			if (!Economy.accountExist(account))
			{
				Economy.newAccount(account);
			}
		}
		return true;
	}

	//simply switches the name to an account type?
	public Account getAccount(String name)
	{
		Account Account = null;
		Account = new Account(name);
		hasAccount(name);
		return Account;
	}

	//Fake remove account
	public void removeAccount(String name)
	{
		if (!existCheck.exist(name))
		{
			if (Economy.accountExist(name))
			{
				Economy.removeAccount(name);
			}
			return;
		}
		Economy.setMoney(name, 0);
	}

	public void createAccount(String account)
	{
		if (!Economy.accountExist(account))
		{
			Economy.newAccount(account);
		}
	}
}
