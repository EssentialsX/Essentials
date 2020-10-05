package com.earth2me.essentials.api;

import com.earth2me.essentials.EssentialsUserConf;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.base.Charsets;
import net.ess3.api.IEssentials;
import net.ess3.api.MaxMoneyException;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.entity.Player;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * You should use Vault instead of directly using this class.
 */
public class Economy {
    public Economy() {
    }

    private static final Logger logger = Logger.getLogger("Essentials");
    private static IEssentials ess;
    private static final String noCallBeforeLoad = "Essentials API is called before Essentials is loaded.";
    public static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    /**
     * @param aEss the ess to set
     */
    public static void setEss(IEssentials aEss) {
        ess = aEss;
    }

    private static void createNPCFile(String name) {
        File folder = new File(ess.getDataFolder(), "userdata");
        name = StringUtil.safeString(name);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        UUID npcUUID = UUID.nameUUIDFromBytes(("NPC:" + name).getBytes(Charsets.UTF_8));
        EssentialsUserConf npcConfig = new EssentialsUserConf(name, npcUUID, new File(folder, npcUUID.toString() + ".yml"));
        npcConfig.load();
        npcConfig.setProperty("npc", true);
        npcConfig.setProperty("lastAccountName", name);
        npcConfig.setProperty("money", ess.getSettings().getStartingBalance());
        npcConfig.forceSave();
        ess.getUserMap().trackUUID(npcUUID, name, false);
    }

    private static void deleteNPC(String name) {
        User user = ess.getUser(name);
        user.reset();
    }

    private static User getUserByName(String name) {
        if (ess == null) {
            throw new RuntimeException(noCallBeforeLoad);
        }
        if (name == null) {
            throw new RuntimeException("Economy username cannot be null");
        }

        User user = ess.getUser(name);
        if (user == null) {
            /*
                Attempt lookup using UUID - this prevents balance resets when accessing economy
                via Vault during player join.
                See: https://github.com/EssentialsX/Essentials/issues/2400
            */
            Player player = ess.getServer().getPlayerExact(name);
            if (player != null) {
                user = ess.getUser(player.getUniqueId());
                if (user != null) {
                    logger.info(String.format("[Economy] Found player %s by UUID %s but not by their actual name - they may have changed their username", name, player.getUniqueId().toString()));
                }
            }
        }

        return user;
    }

    /**
     * Returns the balance of a user
     *
     * @param name Name of the user
     *
     * @return balance
     *
     * @throws UserDoesNotExistException
     */
    @Deprecated
    public static double getMoney(String name) throws UserDoesNotExistException {
        BigDecimal exactAmount = getMoneyExact(name);
        double amount = exactAmount.doubleValue();
        if (new BigDecimal(amount).compareTo(exactAmount) > 0) {
            // closest double is bigger than the exact amount
            // -> get the previous double value to not return more money than the user has
            amount = Math.nextAfter(amount, Double.NEGATIVE_INFINITY);
        }
        return amount;
    }

    public static BigDecimal getMoneyExact(String name) throws UserDoesNotExistException {
        User user = getUserByName(name);
        if (user == null) {
            throw new UserDoesNotExistException(name);
        }
        return user.getMoney();
    }

    /**
     * Sets the balance of a user
     *
     * @param name    Name of the user
     * @param balance The balance you want to set
     *
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    @Deprecated
    public static void setMoney(String name, double balance) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            setMoney(name, BigDecimal.valueOf(balance));
        } catch (ArithmeticException e) {
            logger.log(Level.WARNING, "Failed to set balance of " + name + " to " + balance + ": " + e.getMessage(), e);
        }
    }

    public static void setMoney(String name, BigDecimal balance) throws UserDoesNotExistException, NoLoanPermittedException {
        User user = getUserByName(name);
        if (user == null) {
            throw new UserDoesNotExistException(name);
        }
        if (balance.compareTo(ess.getSettings().getMinMoney()) < 0) {
            throw new NoLoanPermittedException();
        }
        if (balance.signum() < 0 && !user.isAuthorized("essentials.eco.loan")) {
            throw new NoLoanPermittedException();
        }
        try {
            user.setMoney(balance, UserBalanceUpdateEvent.Cause.API);
        } catch (MaxMoneyException ex) {
            //TODO: Update API to show max balance errors
        }
        Trade.log("API", "Set", "API", name, new Trade(balance, ess), null, null, null, ess);
    }

    /**
     * Adds money to the balance of a user
     *
     * @param name   Name of the user
     * @param amount The money you want to add
     *
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    @Deprecated
    public static void add(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            add(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARNING, "Failed to add " + amount + " to balance of " + name + ": " + e.getMessage(), e);
        }
    }

    public static void add(String name, BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException, ArithmeticException {
        BigDecimal result = getMoneyExact(name).add(amount, MATH_CONTEXT);
        setMoney(name, result);
        Trade.log("API", "Add", "API", name, new Trade(amount, ess), null, null, null, ess);
    }

    /**
     * Substracts money from the balance of a user
     *
     * @param name   Name of the user
     * @param amount The money you want to substract
     *
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    @Deprecated
    public static void subtract(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            substract(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARNING, "Failed to substract " + amount + " of balance of " + name + ": " + e.getMessage(), e);
        }
    }

    public static void substract(String name, BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException, ArithmeticException {
        BigDecimal result = getMoneyExact(name).subtract(amount, MATH_CONTEXT);
        setMoney(name, result);
        Trade.log("API", "Subtract", "API", name, new Trade(amount, ess), null, null, null, ess);
    }

    /**
     * Divides the balance of a user by a value
     *
     * @param name  Name of the user
     * @param amount The balance is divided by this value
     *
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    @Deprecated
    public static void divide(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            divide(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARNING, "Failed to divide balance of " + name + " by " + amount + ": " + e.getMessage(), e);
        }
    }

    public static void divide(String name, BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException, ArithmeticException {
        BigDecimal result = getMoneyExact(name).divide(amount, MATH_CONTEXT);
        setMoney(name, result);
        Trade.log("API", "Divide", "API", name, new Trade(amount, ess), null, null, null, ess);
    }

    /**
     * Multiplies the balance of a user by a value
     *
     * @param name  Name of the user
     * @param amount The balance is multiplied by this value
     *
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    @Deprecated
    public static void multiply(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            multiply(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARNING, "Failed to multiply balance of " + name + " by " + amount + ": " + e.getMessage(), e);
        }
    }

    public static void multiply(String name, BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException, ArithmeticException {
        BigDecimal result = getMoneyExact(name).multiply(amount, MATH_CONTEXT);
        setMoney(name, result);
        Trade.log("API", "Multiply", "API", name, new Trade(amount, ess), null, null, null, ess);
    }

    /**
     * Resets the balance of a user to the starting balance
     *
     * @param name Name of the user
     *
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    public static void resetBalance(String name) throws UserDoesNotExistException, NoLoanPermittedException {
        if (ess == null) {
            throw new RuntimeException(noCallBeforeLoad);
        }
        setMoney(name, ess.getSettings().getStartingBalance());
        Trade.log("API", "Reset", "API", name, new Trade(BigDecimal.ZERO, ess), null, null, null, ess);
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should have
     *
     * @return true, if the user has more or an equal amount of money
     *
     * @throws UserDoesNotExistException If a user by that name does not exists
     */
    @Deprecated
    public static boolean hasEnough(String name, double amount) throws UserDoesNotExistException {
        try {
            return hasEnough(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARNING, "Failed to compare balance of " + name + " with " + amount + ": " + e.getMessage(), e);
            return false;
        }
    }

    public static boolean hasEnough(String name, BigDecimal amount) throws UserDoesNotExistException, ArithmeticException {
        return amount.compareTo(getMoneyExact(name)) <= 0;
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should have
     *
     * @return true, if the user has more money
     *
     * @throws UserDoesNotExistException If a user by that name does not exists
     */
    @Deprecated
    public static boolean hasMore(String name, double amount) throws UserDoesNotExistException {
        try {
            return hasMore(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARNING, "Failed to compare balance of " + name + " with " + amount + ": " + e.getMessage(), e);
            return false;
        }
    }

    public static boolean hasMore(String name, BigDecimal amount) throws UserDoesNotExistException, ArithmeticException {
        return amount.compareTo(getMoneyExact(name)) < 0;
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should not have
     *
     * @return true, if the user has less money
     *
     * @throws UserDoesNotExistException If a user by that name does not exists
     */
    @Deprecated
    public static boolean hasLess(String name, double amount) throws UserDoesNotExistException {
        try {
            return hasLess(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARNING, "Failed to compare balance of " + name + " with " + amount + ": " + e.getMessage(), e);
            return false;
        }
    }

    public static boolean hasLess(String name, BigDecimal amount) throws UserDoesNotExistException, ArithmeticException {
        return amount.compareTo(getMoneyExact(name)) > 0;
    }

    /**
     * Test if the user has a negative balance
     *
     * @param name Name of the user
     *
     * @return true, if the user has a negative balance
     *
     * @throws UserDoesNotExistException If a user by that name does not exists
     */
    public static boolean isNegative(String name) throws UserDoesNotExistException {
        return getMoneyExact(name).signum() < 0;
    }

    /**
     * Formats the amount of money like all other Essentials functions. Example: $100000 or $12345.67
     *
     * @param amount The amount of money
     *
     * @return Formatted money
     */
    @Deprecated
    public static String format(double amount) {
        try {
            return format(BigDecimal.valueOf(amount));
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Failed to display " + amount + ": " + e.getMessage(), e);
            return "NaN";
        }
    }

    public static String format(BigDecimal amount) {
        if (ess == null) {
            throw new RuntimeException(noCallBeforeLoad);
        }
        return NumberUtil.displayCurrency(amount, ess);
    }

    /**
     * Test if a player exists to avoid the UserDoesNotExistException
     *
     * @param name Name of the user
     *
     * @return true, if the user exists
     */
    public static boolean playerExists(String name) {
        return getUserByName(name) != null;
    }

    /**
     * Test if a player is a npc
     *
     * @param name Name of the player
     *
     * @return true, if it's a npc
     *
     * @throws UserDoesNotExistException
     */
    public static boolean isNPC(String name) throws UserDoesNotExistException {
        User user = getUserByName(name);
        if (user == null) {
            throw new UserDoesNotExistException(name);
        }
        return user.isNPC();
    }

    /**
     * Creates dummy files for a npc, if there is no player yet with that name.
     *
     * @param name Name of the player
     *
     * @return true, if a new npc was created
     */
    public static boolean createNPC(String name) {
        User user = getUserByName(name);
        if (user == null) {
            createNPCFile(name);
            return true;
        }
        return false;
    }

    /**
     * Deletes a user, if it is marked as npc.
     *
     * @param name Name of the player
     *
     * @throws UserDoesNotExistException
     */
    public static void removeNPC(String name) throws UserDoesNotExistException {
        User user = getUserByName(name);
        if (user == null) {
            throw new UserDoesNotExistException(name);
        }
        deleteNPC(name);
    }
}
