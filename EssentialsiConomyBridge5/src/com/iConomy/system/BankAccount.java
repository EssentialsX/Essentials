package com.iConomy.system;

import com.earth2me.essentials.api.Economy;


public class BankAccount
{
	private String BankName;
	private String BankId;
	private String AccountName;

	public BankAccount(String BankName, String BankId, String AccountName)
	{
		this.BankName = BankName;
		this.BankId = BankId;
		this.AccountName = AccountName;
	}

	public String getBankName()
	{
		return this.BankName;
	}

	public String getBankId()
	{
		return this.BankId;
	}

	public void getAccountName(String AccountName)
	{
		this.AccountName = AccountName;
	}

	public Holdings getHoldings()
	{
		return new Holdings(this.BankId, this.AccountName, true);
	}

	public void remove(String name)
	{
		if (Economy.accountExist(BankId))
		{
			Economy.removeAccount(BankId);
		}
		return;
	}
}
