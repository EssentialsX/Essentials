package com.nijiko.coelho.iConomy;

import org.bukkit.Bukkit;

public class existCheck {

	//We have to make sure the user exists!

	public static boolean exist(String name){
		if (name==null){
			System.out.println("Essentials iConpomy Bridge - Whatever plugin is calling for users that are null is BROKEN!");
			return false;
		}
			if (Bukkit.getServer().getPlayer(name)!=null){
				return true;
			}
		return false;
	}
}