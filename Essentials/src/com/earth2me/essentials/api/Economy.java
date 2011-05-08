package com.earth2me.essentials.api;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import org.bukkit.Bukkit;


public class Economy
{
	protected static Essentials ess = Essentials.getStatic();

	//Does the file exists?
	protected static boolean accountCreated(String name)
	{
		File folder = new File(ess.getDataFolder(), "userdata");
		File account = new File(folder, name.toLowerCase() + ".yml");
		return account.exists();
	}

	//We create the file for the NPC
	protected static void createAccount(String name)
	{

		//Where we will store npc accounts!

		File folder = new File(ess.getDataFolder(), "userdata");
		File npcFile = new File(folder, name + ".yml");

		try
		{
			if (!npcFile.createNewFile())
			{
				System.out.println("Failed file creation");
			}
			return;
		}
		catch (IOException e)
		{
			System.out.println("Could not create Non-player account file!");
		}
		FileWriter fileWriter = null;
		BufferedWriter bufferWriter = null;
		try
		{
			if (!npcFile.exists())
			{
				npcFile.createNewFile();
			}

			fileWriter = new FileWriter(npcFile);
			bufferWriter = new BufferedWriter(fileWriter);

			//This is the default for NPC's, 0

			bufferWriter.append("money: ");
			bufferWriter.append(((Integer)0).toString());
			bufferWriter.newLine();
		}
		catch (IOException e)
		{
			System.out.println("Exception on config creation: ");
		}
		finally
		{
			try
			{
				if (bufferWriter != null)
				{
					bufferWriter.flush();
					bufferWriter.close();
				}

				if (fileWriter != null)
				{
					fileWriter.close();
				}
			}
			catch (IOException e)
			{
				System.out.println("IO Exception writing file: " + npcFile.getName());
			}
		}
	}

	//Convert a string into an essentials User
	protected static User usrConv(String name)
	{
		User user = null;
		if (Bukkit.getServer().getPlayer(name) != null)
		{
			user = ess.getUser(Bukkit.getServer().getPlayer(name));
			return user;
		}
		else
		{
			user = ess.getOfflineUser(name);
		}
		return user;
	}

	//We have to make sure the user exists, or they are an NPC!
	public static boolean exist(String name)
	{

		if (name == null)
		{
			System.out.println("EcoAPI - Whatever plugin is calling for users that are null is BROKEN!");
			return false;
		}
		if (Bukkit.getServer().getPlayer(name) != null)
		{
			return true;
		}
		return false;
	}

	//Eco return balance
	public static double getMoney(String name)
	{
		if (!exist(name))
		{
			if (accountCreated(name))
			{
				User user = usrConv(name);
				return user.getMoney();
			}
			return 0;
		}
		User user = usrConv(name);
		return user.getMoney();
	}

	//Eco Set Money
	public static void setMoney(String name, double bal)
	{
		if (!exist(name))
		{
			if (accountCreated(name))
			{
				User user = usrConv(name);
				user.setMoney(bal);
			}
			return;
		}
		User user = usrConv(name);
		user.setMoney(bal);
		return;
	}

	//Eco add balance
	public static void add(String name, double money)
	{
		double result;
		if (!exist(name))
		{
			if (accountCreated(name))
			{				
				result = getMoney(name) + money;
				User user = usrConv(name);
				user.setMoney(money);
			}
			return;
		}
		result = getMoney(name) + money;
		User user = usrConv(name);
		user.setMoney(result);
		return;
	}

	//Eco divide balance
	public static void divide(String name, double money)
	{
		double result;
		if (!exist(name))
		{
			if (accountCreated(name))
			{
				result = getMoney(name)/ money;
				User user = usrConv(name);
				user.setMoney(result);
				return;
			}
			return;
		}
		result = getMoney(name) / money;
		User user = usrConv(name);
		user.setMoney(result);
		return;
	}

	//Eco multiply balance
	public static void multiply(String name, double money)
	{
		double result;
		if (!exist(name))
		{
			if (accountCreated(name))
			{
				result = getMoney(name) * money;
				User user = usrConv(name);
				user.setMoney(result);
				return;
			}
			return;
		}
		result = getMoney(name) * money;
		User user = usrConv(name);
		user.setMoney(result);
		return;
	}

	//Eco subtract balance
	public static void subtract(String name, double money)
	{
		double result;
		if (!exist(name))
		{
			if (accountCreated(name))
			{
				result = getMoney(name) - money;
				User user = usrConv(name);
				user.setMoney(result);
				return;
			}
			return;
		}
		result = getMoney(name) - money;
		User user = usrConv(name);
		user.setMoney(result);
		return;
	}

	//Eco reset balance!
	public static void resetBalance(String name)
	{
		setMoney(name, 0);
	}

	//Eco has enough check
	public static boolean hasEnough(String name, double amount)
	{
		return amount <= getMoney(name);
	}

	//Eco hasMore balance check
	public static boolean hasMore(String name, double amount)
	{
		return amount < getMoney(name);
	}

	//Eco hasLess balance check
	public static boolean hasLess(String name, double amount)
	{
		return amount > getMoney(name);
	}

	//Eco currency
	public static String getCurrency()
	{
		return ess.getSettings().getCurrency();
	}

	//Eco currency Plural
	public static String getCurrencyPlural()
	{
		return ess.getSettings().getCurrencyPlural();
	}

	//Eco is negative check!
	public static boolean isNegative(String name)
	{
		return getMoney(name) < 0.0;
	}

	//Eco Formatter
	public static String format(double amount)
	{
		DecimalFormat ecoForm = new DecimalFormat("#,##0.##");
		String formed = ecoForm.format(amount);
		if (formed.endsWith("."))
		{
			formed = formed.substring(0, formed.length() - 1);
		}
		return formed + " " + ((amount <= 1 && amount >= -1) ? getCurrency() : getCurrencyPlural());
	}

	//************************!WARNING!**************************
	//**********DO NOT USING THE FOLLOWING FOR PLAYERS!**********
	//**************THESE ARE FOR NPC ACCOUNTS ONLY!*************
	//Eco account exist for NPCs ONLY!
	public static boolean accountExist(String account)
	{
		return accountCreated(account);
	}

	//Eco NPC account creator!  Will return false if it already exists.
	public static boolean newAccount(String account)
	{

		if (!exist(account))
		{
			if (!accountCreated(account))
			{
				createAccount(account);
				return true;
			}
			return false;
		}
		return false;
	}

	//Eco remove account, only use this for NPCS!
	public static void removeAccount(String name)
	{
		if (!exist(name))
		{
			if (accountCreated(name))
			{
				File folder = new File(ess.getDataFolder(), "userdata");
				File account = new File(folder, Util.sanitizeFileName(name) + ".yml");
				account.delete();
			}
		}
		return;
	}
}
