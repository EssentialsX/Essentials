package com.nijiko.coelho.iConomy;

import org.bukkit.plugin.java.JavaPlugin;
import com.nijiko.coelho.iConomy.system.Bank;

//This is not iConomy and I take NO credit for iConomy!
//This is FayConomy, a iConomy Essentials Eco bridge!
//@author Xeology

//Pretend we are iConomy

public class iConomy extends JavaPlugin{
	public static Bank Bank=null;

	//This is for the Essentials to detect FayConomy!

	public static boolean isFay(){
		return true;
	}

	@Override
	public void onDisable() {		
	}

	@Override
	public void onEnable() {
		Bank=new Bank();

		//Can not announce my plugin.yml file, this is NOT iConomy!
		
		System.out.println("Essentials iConomy Bridge v1.0 iz in ur Bukkitz emulating ur iConomyz!");
	}
	
	//Fake bank
	
    public static Bank getBank() {
        return Bank;
    }
}
