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
    public static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;
    private static final Logger logger = Logger.getLogger("Essentials");
    private static final String noCallBeforeLoad = "Essentials API is called before Essentials is loaded.";
    private static IEssentials ess;

    protected Economy() {
    }

    /**
     * @param aEss the ess to set
     */
    public static void setEss(final IEssentials aEss) {
        ess = aEss;
    }

    private static void createNPCFile(String name) {
        final File folder = new File(ess.getDataFolder(), "userdata");
        name = StringUtil.safeString(name);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                throw new RuntimeException("Error while creating userdata directory!");
            }
        }
        final UUID npcUUID = UUID.nameUUIDFromBytes(("NPC:" + name).getBytes(Charsets.UTF_8));
        final EssentialsUserConf npcConfig = new EssentialsUserConf(name, npcUUID, new File(folder, npcUUID.toString() + ".yml"));
        npcConfig.load();
        npcConfig.setProperty("npc", true);
        npcConfig.setProperty("lastAccountName", name);
        npcConfig.setProperty("money", ess.getSettings().getStartingBalance());
        npcConfig.forceSave();
        ess.getUserMap().trackUUID(npcUUID, name, false);
    }

    private static void deleteNPC(final String name) {
        final User user = ess.getUser(name);
        user.reset();
    }

    private static User getUserByName(final String name) {
        if (ess == null) {
            throw new RuntimeException(noCallBeforeLoad);
        }
        if (name == null) {
            throw new IllegalArgumentException("Economy username cannot be null");
        }

        User user = ess.getUser(name);
        if (user == null) {
            /*
                Attempt lookup using UUID - this prevents balance resets when accessing economy
                via Vault during player join.
                See: https://github.com/EssentialsX/Essentials/issues/2400
            */
            final Player player = ess.getServer().getPlayerExact(name);
            if (player != null) {
                user = ess.getUser(player.getUniqueId());
                if (user != null) {
                    logger.info(String.format("[Economy] Found player %s by UUID %s but not by their actual name - they may have changed their username", name, player.getUniqueId().toString()));
                }
            }
        }

        return user;
    }

    private static User getUserByUUID(final UUID uuid) {
        if (ess == null) {
            throw new RuntimeException(noCallBeforeLoad);
        }
        if (uuid == null) {
            throw new IllegalArgumentException("Economy uuid cannot be null");
        }
        return ess.getUser(uuid);
    }

    /**
     * Returns the balance of a user
     *
     * @param name Name of the user
     * @return balance
     * @throws UserDoesNotExistException
     * @deprecated Use {@link Economy#getMoneyExact(UUID)} or {@link Economy#getMoneyExact(User)}
     */
    @Deprecated
    public static double getMoney(final String name) throws UserDoesNotExistException {
        final BigDecimal exactAmount = getMoneyExact(name);
        double amount = exactAmount.doubleValue();
        if (new BigDecimal(amount).compareTo(exactAmount) > 0) {
            // closest double is bigger than the exact amount
            // -> get the previous double value to not return more money than the user has
            amount = Math.nextAfter(amount, Double.NEGATIVE_INFINITY);
        }
        return amount;
    }

    /**
     * @param name Name of user
     * @return Exact balance of user
     * @throws UserDoesNotExistException
     * @deprecated Usernames can change, use {@link Economy#getMoneyExact(UUID)} or {@link Economy#getMoneyExact(User)}
     */
    @Deprecated
    public static BigDecimal getMoneyExact(final String name) throws UserDoesNotExistException {
        final User user = getUserByName(name);
        if (user == null) {
            throw new UserDoesNotExistException(name);
        }
        return getMoneyExact(user);
    }

    /**
     * Get the exact balance of the account with the given UUID.
     *
     * @param uuid The UUID of the user account to retrieve the balance for
     * @return The account's balance
     * @throws UserDoesNotExistException If the user does not exist
     */
    public static BigDecimal getMoneyExact(final UUID uuid) throws UserDoesNotExistException {
        final User user = getUserByUUID(uuid);
        if (user == null) {
            throw new UserDoesNotExistException(uuid);
        }
        return getMoneyExact(user);
    }

    /**
     * Get the exact balance of the account with the given UUID.
     *
     * @param user The user account to retrieve the balance for
     * @return The account's balance
     */
    public static BigDecimal getMoneyExact(final User user) {
        if (user == null) {
            throw new IllegalArgumentException("Economy user cannot be null");
        }
        return user.getMoney();
    }

    /**
     * Sets the balance of a user
     *
     * @param name    Name of the user
     * @param balance The balance you want to set
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     * @deprecated Use {@link Economy#setMoney(UUID, BigDecimal)} or {@link Economy#setMoney(User, BigDecimal)}
     */
    @Deprecated
    public static void setMoney(final String name, final double balance) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            setMoney(name, BigDecimal.valueOf(balance));
        } catch (final ArithmeticException e) {
            logger.log(Level.WARNING, "Failed to set balance of " + name + " to " + balance + ": " + e.getMessage(), e);
        }
    }

    /**
     * Sets the balance of a user
     *
     * @param name    Name of user
     * @param balance The balance you want to set
     * @throws UserDoesNotExistException If a user by that name does not exist
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     * @deprecated Usernames can change use {@link Economy#setMoney(UUID, BigDecimal)} or {@link Economy#setMoney(User, BigDecimal)}
     */
    @Deprecated
    public static void setMoney(final String name, final BigDecimal balance) throws UserDoesNotExistException, NoLoanPermittedException {
        final User user = getUserByName(name);
        if (user == null) {
            throw new UserDoesNotExistException(name);
        }
        setMoney(user, balance);
    }

    /**
     * Sets the balance of a user
     *
     * @param uuid    UUID of user
     * @param balance The balance you want to set
     * @throws UserDoesNotExistException If a user by that uuid does not exist
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    public static void setMoney(final UUID uuid, final BigDecimal balance) throws NoLoanPermittedException, UserDoesNotExistException {
        final User user = getUserByUUID(uuid);
        if (user == null) {
            throw new UserDoesNotExistException(uuid);
        }
        setMoney(user, balance);
    }

    /**
     * Sets the balance of a user
     *
     * @param user    User
     * @param balance The balance you want to set
     * @throws NoLoanPermittedException If the user is not allowed to have a negative balance
     */
    public static void setMoney(final User user, final BigDecimal balance) throws NoLoanPermittedException {
        if (user == null) {
            throw new IllegalArgumentException("Economy user cannot be null");
        }
        if (balance.compareTo(ess.getSettings().getMinMoney()) < 0) {
            throw new NoLoanPermittedException();
        }
        if (balance.signum() < 0 && !user.isAuthorized("essentials.eco.loan")) {
            throw new NoLoanPermittedException();
        }
        try {
            user.setMoney(balance, UserBalanceUpdateEvent.Cause.API);
        } catch (final MaxMoneyException ex) {
            //TODO: Update API to show max balance errors
        }
        Trade.log("API", "Set", "API", user.getName(), new Trade(balance, ess), null, null, null, ess);
    }

    /**
     * Adds money to the balance of a user
     * <p>
     * Use {@link Economy#add(UUID, BigDecimal)} or {@link Economy#add(User, BigDecimal)}
     *
     * @param name   Name of the user
     * @param amount The money you want to add
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    @Deprecated
    public static void add(final String name, final double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            add(name, BigDecimal.valueOf(amount));
        } catch (final ArithmeticException e) {
            logger.log(Level.WARNING, "Failed to add " + amount + " to balance of " + name + ": " + e.getMessage(), e);
        }
    }

    /**
     * Adds money to the balance of a user
     *
     * @param name   Name of the user
     * @param amount The amount of money to be added to the user's account
     * @throws UserDoesNotExistException If a user by that name does not exist
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     * @throws ArithmeticException
     * @deprecated Usernames can change, use {@link Economy#add(UUID, BigDecimal)} or {@link Economy#add(User, BigDecimal)}
     */
    @Deprecated
    public static void add(final String name, final BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException, ArithmeticException {
        final User user = getUserByName(name);
        if (user == null) {
            throw new UserDoesNotExistException(name);
        }
        add(user, amount);
    }

    /**
     * Adds money to the balance of a user
     *
     * @param uuid   UUID of the user
     * @param amount The money you want to add
     * @throws UserDoesNotExistException If a user by that uuid does not exist
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     * @throws ArithmeticException
     */
    public static void add(final UUID uuid, final BigDecimal amount) throws NoLoanPermittedException, ArithmeticException, UserDoesNotExistException {
        final User user = getUserByUUID(uuid);
        if (user == null) {
            throw new UserDoesNotExistException(uuid);
        }
        add(user, amount);
    }

    /**
     * Adds money to the balance of a user
     *
     * @param user   User
     * @param amount The money you want to add
     * @throws NoLoanPermittedException If the user is not allowed to have a negative balance
     * @throws ArithmeticException
     * @deprecated Usernames can change, use {@link Economy#add(UUID, BigDecimal)} or {@link Economy#add(User, BigDecimal)}
     */
    public static void add(final User user, final BigDecimal amount) throws NoLoanPermittedException, ArithmeticException {
        if (user == null) {
            throw new IllegalArgumentException("Economy user cannot be null");
        }
        final BigDecimal result = getMoneyExact(user).add(amount, MATH_CONTEXT);
        setMoney(user, result);
        Trade.log("API", "Add", "API", user.getName(), new Trade(amount, ess), null, null, null, ess);
    }

    /**
     * Subtracts money from the balance of a user
     *
     * @param name   Name of the user
     * @param amount The money you want to subtract
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     * @deprecated Use {@link Economy#subtract(UUID, BigDecimal)} or {@link Economy#subtract(User, BigDecimal)}
     */
    @Deprecated
    public static void subtract(final String name, final double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            substract(name, BigDecimal.valueOf(amount));
        } catch (final ArithmeticException e) {
            logger.log(Level.WARNING, "Failed to subtract " + amount + " of balance of " + name + ": " + e.getMessage(), e);
        }
    }

    /**
     * Subtracts money from the balance of a user
     *
     * @param name   Name of the user
     * @param amount The money you want to subtract
     * @throws UserDoesNotExistException If a user by that name does not exist
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     * @throws ArithmeticException
     * @deprecated Usernames can change, use {@link Economy#subtract(UUID, BigDecimal)} or {@link Economy#subtract(User, BigDecimal)}
     */
    @Deprecated
    public static void substract(final String name, final BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException, ArithmeticException {
        final BigDecimal result = getMoneyExact(name).subtract(amount, MATH_CONTEXT);
        setMoney(name, result);
        Trade.log("API", "Subtract", "API", name, new Trade(amount, ess), null, null, null, ess);
    }

    /**
     * Subtracts money from the balance of a user
     *
     * @param uuid   UUID of the user
     * @param amount The money you want to subtract
     * @throws UserDoesNotExistException If a user by that UUID does not exist
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     * @throws ArithmeticException
     */
    public static void subtract(final UUID uuid, final BigDecimal amount) throws NoLoanPermittedException, ArithmeticException, UserDoesNotExistException {
        final User user = getUserByUUID(uuid);
        if (user == null) {
            throw new UserDoesNotExistException(uuid);
        }
        subtract(user, amount);
    }

    /**
     * Subtracts money from the balance of a user
     *
     * @param user   User
     * @param amount The money you want to subtract
     * @throws NoLoanPermittedException If the user is not allowed to have a negative balance
     * @throws ArithmeticException
     */
    public static void subtract(final User user, final BigDecimal amount) throws NoLoanPermittedException, ArithmeticException {
        if (user == null) {
            throw new IllegalArgumentException("Economy user cannot be null");
        }
        final BigDecimal result = getMoneyExact(user).subtract(amount, MATH_CONTEXT);
        setMoney(user, result);
        Trade.log("API", "Subtract", "API", user.getName(), new Trade(amount, ess), null, null, null, ess);
    }

    /**
     * Divides the balance of a user by a value
     *
     * @param name   Name of the user
     * @param amount The balance is divided by this value
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     * @deprecated Use {@link Economy#divide(UUID, BigDecimal)} or {@link Economy#divide(User, BigDecimal)}
     */
    @Deprecated
    public static void divide(final String name, final double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            divide(name, BigDecimal.valueOf(amount));
        } catch (final ArithmeticException e) {
            logger.log(Level.WARNING, "Failed to divide balance of " + name + " by " + amount + ": " + e.getMessage(), e);
        }
    }

    /**
     * Divides the balance of a user by a value
     *
     * @param name   Name of the user
     * @param amount The balance is divided by this value
     * @throws UserDoesNotExistException If a user by that name does not exist
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     * @throws ArithmeticException
     * @deprecated Usernames can change, use {@link Economy#divide(UUID, BigDecimal)} or {@link Economy#divide(User, BigDecimal)}
     */
    @Deprecated
    public static void divide(final String name, final BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException, ArithmeticException {
        final User user = getUserByName(name);
        if (user == null) {
            throw new UserDoesNotExistException(name);
        }
        divide(user, amount);
    }

    /**
     * Divides the balance of a user by a value
     *
     * @param uuid   Name of the user
     * @param amount The balance is divided by this value
     * @throws UserDoesNotExistException If a user by that UUID does not exist
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     * @throws ArithmeticException
     */
    public static void divide(final UUID uuid, final BigDecimal amount) throws NoLoanPermittedException, ArithmeticException, UserDoesNotExistException {
        final User user = getUserByUUID(uuid);
        if (user == null) {
            throw new UserDoesNotExistException(uuid);
        }
        divide(user, amount);
    }

    /**
     * Divides the balance of a user by a value
     *
     * @param user   Name of the user
     * @param amount The balance is divided by this value
     * @throws NoLoanPermittedException If the user is not allowed to have a negative balance
     * @throws ArithmeticException
     */
    public static void divide(final User user, final BigDecimal amount) throws NoLoanPermittedException, ArithmeticException {
        if (user == null) {
            throw new IllegalArgumentException("Economy user cannot be null");
        }
        final BigDecimal result = getMoneyExact(user).divide(amount, MATH_CONTEXT);
        setMoney(user, result);
        Trade.log("API", "Divide", "API", user.getName(), new Trade(amount, ess), null, null, null, ess);
    }

    /**
     * Multiplies the balance of a user by a value
     *
     * @param name   Name of the user
     * @param amount The balance is multiplied by this value
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     * @deprecated Use {@link Economy#multiply(UUID, BigDecimal)} or {@link Economy#multiply(User, BigDecimal)}
     */
    @Deprecated
    public static void multiply(final String name, final double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            multiply(name, BigDecimal.valueOf(amount));
        } catch (final ArithmeticException e) {
            logger.log(Level.WARNING, "Failed to multiply balance of " + name + " by " + amount + ": " + e.getMessage(), e);
        }
    }

    /**
     * Multiplies the balance of a user by a value
     *
     * @param name   Name of the user
     * @param amount The balance is multiplied by the this value
     * @throws UserDoesNotExistException If a user by that name does not exist
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     * @throws ArithmeticException
     * @deprecated Usernames can change, use {@link Economy#multiply(UUID, BigDecimal)} or {@link Economy#multiply(User, BigDecimal)}
     */
    @Deprecated
    public static void multiply(final String name, final BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException, ArithmeticException {
        final User user = getUserByName(name);
        if (user == null) {
            throw new UserDoesNotExistException(name);
        }
        multiply(user, amount);
    }

    /**
     * Multiplies the balance of a user by a value
     *
     * @param uuid   Name of the user
     * @param amount The balance is multiplied by the this value
     * @throws UserDoesNotExistException If a user by that uuid does not exist
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     * @throws ArithmeticException
     */
    public static void multiply(final UUID uuid, final BigDecimal amount) throws NoLoanPermittedException, ArithmeticException, UserDoesNotExistException {
        final User user = getUserByUUID(uuid);
        if (user == null) {
            throw new UserDoesNotExistException(uuid);
        }
        multiply(user, amount);
    }

    /**
     * Multiplies the balance of a user by a value
     *
     * @param user   Name of the user
     * @param amount The balance is multiplied by the this value
     * @throws NoLoanPermittedException If the user is not allowed to have a negative balance
     * @throws ArithmeticException
     */
    public static void multiply(final User user, final BigDecimal amount) throws NoLoanPermittedException, ArithmeticException {
        if (user == null) {
            throw new IllegalArgumentException("Economy user cannot be null");
        }
        final BigDecimal result = getMoneyExact(user).multiply(amount, MATH_CONTEXT);
        setMoney(user, result);
        Trade.log("API", "Multiply", "API", user.getName(), new Trade(amount, ess), null, null, null, ess);
    }

    /**
     * Resets the balance of a user to the starting balance
     *
     * @param name Name of the user
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     * @deprecated Usernames can change, use {@link Economy#resetBalance(UUID)} or {@link Economy#resetBalance(User)}
     */
    @Deprecated
    public static void resetBalance(final String name) throws UserDoesNotExistException, NoLoanPermittedException {
        if (ess == null) {
            throw new RuntimeException(noCallBeforeLoad);
        }
        setMoney(name, ess.getSettings().getStartingBalance());
        Trade.log("API", "Reset", "API", name, new Trade(BigDecimal.ZERO, ess), null, null, null, ess);
    }

    /**
     * Resets the balance of a user to the starting balance
     *
     * @param uuid UUID of the user
     * @throws UserDoesNotExistException If a user by that UUID does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    public static void resetBalance(final UUID uuid) throws NoLoanPermittedException, UserDoesNotExistException {
        final User user = getUserByUUID(uuid);
        if (user == null) {
            throw new UserDoesNotExistException(uuid);
        }
        resetBalance(user);
    }

    /**
     * Resets the balance of a user to the starting balance
     *
     * @param user User
     * @throws NoLoanPermittedException If the user is not allowed to have a negative balance
     */
    public static void resetBalance(final User user) throws NoLoanPermittedException {
        if (ess == null) {
            throw new RuntimeException(noCallBeforeLoad);
        }
        if (user == null) {
            throw new IllegalArgumentException("Economy user cannot be null");
        }
        setMoney(user, ess.getSettings().getStartingBalance());
        Trade.log("API", "Reset", "API", user.getName(), new Trade(BigDecimal.ZERO, ess), null, null, null, ess);
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should have
     * @return true, if the user has more or an equal amount of money
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @deprecated Use {@link Economy#hasEnough(UUID, BigDecimal)} or {@link Economy#hasEnough(User, BigDecimal)}
     */
    @Deprecated
    public static boolean hasEnough(final String name, final double amount) throws UserDoesNotExistException {
        try {
            return hasEnough(name, BigDecimal.valueOf(amount));
        } catch (final ArithmeticException e) {
            logger.log(Level.WARNING, "Failed to compare balance of " + name + " with " + amount + ": " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should have
     * @return true, if the user has more or an equal amount of money
     * @throws UserDoesNotExistException If a user by that name does not exist
     * @throws ArithmeticException
     * @deprecated Usernames can change, use {@link Economy#hasEnough(UUID, BigDecimal)} or {@link Economy#hasEnough(User, BigDecimal)}
     */
    public static boolean hasEnough(final String name, final BigDecimal amount) throws UserDoesNotExistException, ArithmeticException {
        final User user = getUserByName(name);
        if (user == null) {
            throw new UserDoesNotExistException(name);
        }
        return hasEnough(user, amount);
    }

    /**
     * @param uuid   UUID of the user
     * @param amount The amount of money the user should have
     * @return true, if the user has more or an equal amount of money
     * @throws UserDoesNotExistException If a user by that UUID does not exist
     * @throws ArithmeticException
     */
    public static boolean hasEnough(final UUID uuid, final BigDecimal amount) throws ArithmeticException, UserDoesNotExistException {
        final User user = getUserByUUID(uuid);
        if (user == null) {
            throw new UserDoesNotExistException(uuid);
        }
        return hasEnough(user, amount);
    }

    /**
     * @param user   User
     * @param amount The amount of money the user should have
     * @return true, if the user has more or an equal amount of money
     * @throws ArithmeticException
     */
    public static boolean hasEnough(final User user, final BigDecimal amount) throws ArithmeticException {
        if (user == null) {
            throw new IllegalArgumentException("Economy user cannot be null");
        }
        return amount.compareTo(getMoneyExact(user)) <= 0;
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should have
     * @return true, if the user has more money
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @deprecated Use {@link Economy#hasMore(UUID, BigDecimal)} or {@link Economy#hasMore(User, BigDecimal)}
     */
    @Deprecated
    public static boolean hasMore(final String name, final double amount) throws UserDoesNotExistException {
        try {
            return hasMore(name, BigDecimal.valueOf(amount));
        } catch (final ArithmeticException e) {
            logger.log(Level.WARNING, "Failed to compare balance of " + name + " with " + amount + ": " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should have
     * @return true, if the user has more money
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws ArithmeticException
     * @deprecated Usernames can change, use {@link Economy#hasMore(UUID, BigDecimal)} or {@link Economy#hasMore(User, BigDecimal)}
     */
    @Deprecated
    public static boolean hasMore(final String name, final BigDecimal amount) throws UserDoesNotExistException, ArithmeticException {
        final User user = getUserByName(name);
        if (user == null) {
            throw new UserDoesNotExistException(name);
        }
        return hasMore(user, amount);
    }

    /**
     * @param uuid   UUID of the user
     * @param amount The amount of money the user should have
     * @return true, if the user has more money
     * @throws UserDoesNotExistException If a user by that UUID does not exists
     * @throws ArithmeticException
     */
    public static boolean hasMore(final UUID uuid, final BigDecimal amount) throws ArithmeticException, UserDoesNotExistException {
        final User user = getUserByUUID(uuid);
        if (user == null) {
            throw new UserDoesNotExistException(uuid);
        }
        return hasMore(user, amount);
    }

    /**
     * @param user   User
     * @param amount The amount of money the user should have
     * @return true, if the user has more money
     * @throws ArithmeticException
     */
    public static boolean hasMore(final User user, final BigDecimal amount) throws ArithmeticException {
        if (user == null) {
            throw new IllegalArgumentException("Economy user cannot be null");
        }
        return amount.compareTo(getMoneyExact(user)) < 0;
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should not have
     * @return true, if the user has less money
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @deprecated Use {@link Economy#hasLess(UUID, BigDecimal)} or {@link Economy#hasLess(User, BigDecimal)}
     */
    @Deprecated
    public static boolean hasLess(final String name, final double amount) throws UserDoesNotExistException {
        try {
            return hasLess(name, BigDecimal.valueOf(amount));
        } catch (final ArithmeticException e) {
            logger.log(Level.WARNING, "Failed to compare balance of " + name + " with " + amount + ": " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should not have
     * @return true, if the user has less money
     * @throws UserDoesNotExistException If a user by that name does not exist
     * @throws ArithmeticException
     * @deprecated Usernames can change, use {@link Economy#hasLess(UUID, BigDecimal)} or {@link Economy#hasLess(User, BigDecimal)}
     */
    @Deprecated
    public static boolean hasLess(final String name, final BigDecimal amount) throws UserDoesNotExistException, ArithmeticException {
        final User user = getUserByName(name);
        if (user == null) {
            throw new UserDoesNotExistException(name);
        }
        return hasLess(user, amount);
    }

    /**
     * @param uuid   UUID of the user
     * @param amount The amount of money the user should not have
     * @return true, if the user has less money
     * @throws UserDoesNotExistException If a user by that UUID does not exist
     * @throws ArithmeticException
     */
    public static boolean hasLess(final UUID uuid, final BigDecimal amount) throws ArithmeticException, UserDoesNotExistException {
        final User user = getUserByUUID(uuid);
        if (user == null) {
            throw new UserDoesNotExistException(uuid);
        }
        return hasLess(user, amount);
    }

    /**
     * @param user   User
     * @param amount The amount of money the user should not have
     * @return true, if the user has less money
     * @throws ArithmeticException
     */
    public static boolean hasLess(final User user, final BigDecimal amount) throws ArithmeticException {
        if (user == null) {
            throw new IllegalArgumentException("Economy user cannot be null");
        }
        return amount.compareTo(getMoneyExact(user)) > 0;
    }

    /**
     * Test if the user has a negative balance
     *
     * @param name Name of the user
     * @return true, if the user has a negative balance
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @deprecated Usernames can change, use {@link Economy#isNegative(UUID)} or {@link Economy#isNegative(User)}
     */
    @Deprecated
    public static boolean isNegative(final String name) throws UserDoesNotExistException {
        final User user = getUserByName(name);
        if (user == null) {
            throw new UserDoesNotExistException(name);
        }
        return isNegative(user);
    }

    /**
     * Test if the user has a negative balance
     *
     * @param uuid UUID of the user
     * @return true, if the user has a negative balance
     * @throws UserDoesNotExistException If a user by that UUID does not exists
     */
    public static boolean isNegative(final UUID uuid) throws UserDoesNotExistException {
        final User user = getUserByUUID(uuid);
        if (user == null) {
            throw new UserDoesNotExistException(uuid);
        }
        return isNegative(user);
    }

    /**
     * Test if the user has a negative balance
     *
     * @param user User
     * @return true, if the user has a negative balance
     */
    public static boolean isNegative(final User user) {
        if (user == null) {
            throw new IllegalArgumentException("Economy user cannot be null");
        }
        return getMoneyExact(user).signum() < 0;
    }

    /**
     * Formats the amount of money like all other Essentials functions. Example: $100000 or $12345.67
     *
     * @param amount The amount of money
     * @return Formatted money
     * @deprecated Use {@link #format(BigDecimal)} if your input is already a {@link BigDecimal}.
     */
    @Deprecated
    public static String format(final double amount) {
        try {
            return format(BigDecimal.valueOf(amount));
        } catch (final NumberFormatException e) {
            logger.log(Level.WARNING, "Failed to display " + amount + ": " + e.getMessage(), e);
            return "NaN";
        }
    }

    /**
     * Formats the amount of money like all other Essentials functions. Example: $100000 or $12345.67
     *
     * @param amount The amount of money
     * @return Formatted money
     */
    public static String format(final BigDecimal amount) {
        if (ess == null) {
            throw new RuntimeException(noCallBeforeLoad);
        }
        return NumberUtil.displayCurrency(amount, ess);
    }

    /**
     * Test if a player exists to avoid the UserDoesNotExistException
     *
     * @param name Name of the user
     * @return true, if the user exists
     * @deprecated Essentials is moving away from username based economy methods. This may be removed in the future.
     */
    @Deprecated
    public static boolean playerExists(final String name) {
        return getUserByName(name) != null;
    }

    /**
     * Test if a player exists to avoid the UserDoesNotExistException
     *
     * @param uuid UUID of the user
     * @return true, if the user exists
     */
    public static boolean playerExists(final UUID uuid) {
        return getUserByUUID(uuid) != null;
    }

    /**
     * Test if a player is a npc
     *
     * @param name Name of the player
     * @return true, if it's a npc
     * @throws UserDoesNotExistException
     */
    public static boolean isNPC(final String name) throws UserDoesNotExistException {
        final User user = getUserByName(name);
        if (user == null) {
            throw new UserDoesNotExistException(name);
        }
        return user.isNPC();
    }

    /**
     * Creates dummy files for a npc, if there is no player yet with that name.
     *
     * @param name Name of the player
     * @return true, if a new npc was created
     */
    public static boolean createNPC(final String name) {
        final User user = getUserByName(name);
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
     * @throws UserDoesNotExistException
     */
    public static void removeNPC(final String name) throws UserDoesNotExistException {
        final User user = getUserByName(name);
        if (user == null) {
            throw new UserDoesNotExistException(name);
        }
        deleteNPC(name);
    }
}
