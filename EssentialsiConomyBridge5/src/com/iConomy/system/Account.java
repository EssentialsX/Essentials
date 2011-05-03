package com.iConomy.system;

import com.earth2me.essentials.api.Economy;
import com.iConomy.existCheck;
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

	/**
	 * Essentials does not support hidden accounts.
	 * @return false
	 */
	public boolean isHidden()
	{
		return false;
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
}
