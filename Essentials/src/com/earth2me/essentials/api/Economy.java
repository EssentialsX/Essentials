package com.earth2me.essentials.api;

import com.earth2me.essentials.EssentialsConf;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;


/**
 * Instead of using this api directly, we recommend to use the register plugin:
 * http://bit.ly/RegisterMethod
 */
public final class Economy
{
	private Economy()
	{
	}
	private static final Logger logger = Logger.getLogger("Minecraft");
	private static IEssentials ess;
	private static final String noCallBeforeLoad = "Essentials API is called before Essentials is loaded.";

	/**
	 * @param aEss the ess to set
	 */
	public static void setEss(IEssentials aEss)
	{
		ess = aEss;
	}

	private static void createNPCFile(String name)
	{
		File folder = new File(ess.getDataFolder(), "userdata");
		if (!folder.exists())
		{
			folder.mkdirs();
		}
		EssentialsConf npcConfig = new EssentialsConf(new File(folder, Util.sanitizeFileName(name) + ".yml"));
		npcConfig.load();
		npcConfig.setProperty("npc", true);
		npcConfig.setProperty("money", ess.getSettings().getStartingBalance());
		npcConfig.save();
	}

	private static void deleteNPC(String name)
	{
		File folder = new File(ess.getDataFolder(), "userdata");
		if (!folder.exists())
		{
			folder.mkdirs();
		}
		File config = new File(folder, Util.sanitizeFileName(name) + ".yml");
		EssentialsConf npcConfig = new EssentialsConf(config);
		npcConfig.load();
		if (npcConfig.hasProperty("npc") && npcConfig.getBoolean("npc", false))
		{
			if (!config.delete())
			{
				logger.log(Level.WARNING, _("deleteFileError", config));
			}
			ess.getUserMap().removeUser(name);
		}
	}

	private static User getUserByName(String name)
	{
		if (ess == null)
		{
			throw new RuntimeException(noCallBeforeLoad);
		}
		User user;
		Player player = ess.getServer().getPlayer(name);
		if (player != null)
		{
			user = ess.getUser(player);
		}
		else
		{
			user = ess.getOfflineUser(name);
		}
		return user;
	}

	/**
	 * Returns the balance of a user
	 * @param name Name of the user
	 * @return balance
	 * @throws UserDoesNotExistException 
	 */
	public static double getMoney(String name) throws UserDoesNotExistException
	{
		User user = getUserByName(name);
		if (user == null)
		{
			throw new UserDoesNotExistException(name);
		}
		return user.getMoney();
	}

	/**
	 * Sets the balance of a user
	 * @param name Name of the user
	 * @param balance The balance you want to set
	 * @throws UserDoesNotExistException If a user by that name does not exists
	 * @throws NoLoanPermittedException If the user is not allowed to have a negative balance
	 */
	public static void setMoney(String name, double balance) throws UserDoesNotExistException, NoLoanPermittedException
	{
		User user = getUserByName(name);
		if (user == null)
		{
			throw new UserDoesNotExistException(name);
		}
		if (balance < ess.getSettings().getMinMoney())
		{
			throw new NoLoanPermittedException();
		}
		if (balance < 0.0 && !user.isAuthorized("essentials.eco.loan"))
		{
			throw new NoLoanPermittedException();
		}
		user.setMoney(balance);
	}

	/**
	 * Adds money to the balance of a user
	 * @param name Name of the user
	 * @param amount The money you want to add
	 * @throws UserDoesNotExistException If a user by that name does not exists
	 * @throws NoLoanPermittedException If the user is not allowed to have a negative balance
	 */
	public static void add(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException
	{
		double result = getMoney(name) + amount;
		setMoney(name, result);
	}

	/**
	 * Substracts money from the balance of a user
	 * @param name Name of the user
	 * @param amount The money you want to substract
	 * @throws UserDoesNotExistException If a user by that name does not exists
	 * @throws NoLoanPermittedException If the user is not allowed to have a negative balance
	 */
	public static void subtract(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException
	{
		double result = getMoney(name) - amount;
		setMoney(name, result);
	}

	/**
	 * Divides the balance of a user by a value
	 * @param name Name of the user
	 * @param value The balance is divided by this value
	 * @throws UserDoesNotExistException If a user by that name does not exists
	 * @throws NoLoanPermittedException If the user is not allowed to have a negative balance
	 */
	public static void divide(String name, double value) throws UserDoesNotExistException, NoLoanPermittedException
	{
		double result = getMoney(name) / value;
		setMoney(name, result);
	}

	/**
	 * Multiplies the balance of a user by a value
	 * @param name Name of the user
	 * @param value The balance is multiplied by this value
	 * @throws UserDoesNotExistException If a user by that name does not exists
	 * @throws NoLoanPermittedException If the user is not allowed to have a negative balance
	 */
	public static void multiply(String name, double value) throws UserDoesNotExistException, NoLoanPermittedException
	{
		double result = getMoney(name) * value;
		setMoney(name, result);
	}

	/**
	 * Resets the balance of a user to the starting balance
	 * @param name Name of the user
	 * @throws UserDoesNotExistException If a user by that name does not exists
	 * @throws NoLoanPermittedException If the user is not allowed to have a negative balance
	 */
	public static void resetBalance(String name) throws UserDoesNotExistException, NoLoanPermittedException
	{
		if (ess == null)
		{
			throw new RuntimeException(noCallBeforeLoad);
		}
		setMoney(name, ess.getSettings().getStartingBalance());
	}

	/**
	 * @param name Name of the user
	 * @param amount The amount of money the user should have
	 * @return true, if the user has more or an equal amount of money
	 * @throws UserDoesNotExistException If a user by that name does not exists
	 */
	public static boolean hasEnough(String name, double amount) throws UserDoesNotExistException
	{
		return amount <= getMoney(name);
	}

	/**
	 * @param name Name of the user
	 * @param amount The amount of money the user should have
	 * @return true, if the user has more money
	 * @throws UserDoesNotExistException If a user by that name does not exists
	 */
	public static boolean hasMore(String name, double amount) throws UserDoesNotExistException
	{
		return amount < getMoney(name);
	}

	/**
	 * @param name Name of the user
	 * @param amount The amount of money the user should not have
	 * @return true, if the user has less money
	 * @throws UserDoesNotExistException If a user by that name does not exists
	 */
	public static boolean hasLess(String name, double amount) throws UserDoesNotExistException
	{
		return amount > getMoney(name);
	}

	/**
	 * Test if the user has a negative balance
	 * @param name Name of the user
	 * @return true, if the user has a negative balance
	 * @throws UserDoesNotExistException If a user by that name does not exists
	 */
	public static boolean isNegative(String name) throws UserDoesNotExistException
	{
		return getMoney(name) < 0.0;
	}

	/**
	 * Formats the amount of money like all other Essentials functions.
	 * Example: $100000 or $12345.67
	 * @param amount The amount of money
	 * @return Formatted money
	 */
	public static String format(double amount)
	{
		if (ess == null)
		{
			throw new RuntimeException(noCallBeforeLoad);
		}
		return Util.displayCurrency(amount, ess);
	}

	/**
	 * Test if a player exists to avoid the UserDoesNotExistException
	 * @param name Name of the user
	 * @return true, if the user exists
	 */
	public static boolean playerExists(String name)
	{
		return getUserByName(name) != null;
	}

	/**
	 * Test if a player is a npc
	 * @param name Name of the player
	 * @return true, if it's a npc
	 * @throws UserDoesNotExistException 
	 */
	public static boolean isNPC(String name) throws UserDoesNotExistException
	{
		User user = getUserByName(name);
		if (user == null)
		{
			throw new UserDoesNotExistException(name);
		}
		return user.isNPC();
	}

	/**
	 * Creates dummy files for a npc, if there is no player yet with that name.
	 * @param name Name of the player
	 * @return true, if a new npc was created
	 */
	public static boolean createNPC(String name)
	{
		User user = getUserByName(name);
		if (user == null)
		{
			createNPCFile(name);
			return true;
		}
		return false;
	}

	/**
	 * Deletes a user, if it is marked as npc. 
	 * @param name Name of the player
	 * @throws UserDoesNotExistException 
	 */
	public static void removeNPC(String name) throws UserDoesNotExistException
	{
		User user = getUserByName(name);
		if (user == null)
		{
			throw new UserDoesNotExistException(name);
		}
		deleteNPC(name);
	}
}
