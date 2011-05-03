package com.iConomy.system;

import com.earth2me.essentials.api.Economy;
import com.iConomy.existCheck;


public class Bank
{
	private String id = null;
	private String name = null;

	public Bank(String name)
	{
		this.id = name;
		this.name = name;
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
		return new Account(name);
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
