package com.nijiko.coelho.iConomy.system;

import com.earth2me.essentials.EcoAPI;
import com.nijiko.coelho.iConomy.existCheck;


public class Bank {

    //The fake formatter

	public String format(double amount) {
		return EcoAPI.format(amount);
    }

	//Fake currency!

    public String getCurrency() {
        return EcoAPI.getCurrency();
    }

	//Fake "does player have an account?" but essentials eco doesnt need to make one, so TRUE, unless its an NPC.

	public boolean hasAccount(String account) {
		if (!existCheck.exist(account)){
			if (!EcoAPI.accountExist(account)){
				EcoAPI.newAccount(account);
			}			
		}
		return true;
	}
	
	//simply switches the name to an account type?
	
	public Account getAccount(String name){
		Account Account=null;
		Account=new Account(name);
		hasAccount(name);
		return Account;
	}
	
	//Fake remove account
	
	public void removeAccount(String name){
		if (!existCheck.exist(name)){
			if (EcoAPI.accountExist(name)){
				EcoAPI.removeAccount(name);
			}
			return;
		}
		EcoAPI.setMoney(name, 0);
	}
	
}
