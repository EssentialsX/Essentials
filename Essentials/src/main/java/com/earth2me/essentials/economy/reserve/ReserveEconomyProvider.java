package com.earth2me.essentials.economy.reserve;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.base.Charsets;
import net.ess3.api.MaxMoneyException;
import net.tnemc.core.Reserve;
import net.tnemc.core.economy.EconomyAPI;
import net.tnemc.core.economy.response.AccountResponse;
import net.tnemc.core.economy.response.EconomyResponse;
import net.tnemc.core.economy.response.GeneralResponse;
import net.tnemc.core.economy.response.HoldingsResponse;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.UUID;

public class ReserveEconomyProvider implements EconomyAPI {

    private final Essentials ess;

    public ReserveEconomyProvider(Essentials plugin) {
        this.ess = plugin;
    }

    public static void register(Essentials plugin) {
        Reserve.instance().registerProvider(new ReserveEconomyProvider(plugin));
    }

    /**
     * @return The name of the Economy implementation.
     */
    @Override
    public String name() {
        return "EssentialsX";
    }

    /**
     * @return The version of Reserve the Economy implementation supports.
     */
    @Override
    public String version() {
        return "0.1.5.4";
    }

    //This is our method to convert UUID -> username for use with Essentials' create account methods.
    private User getUser(String identifier) throws UserDoesNotExistException {

        try {
            final UUID id = UUID.fromString(identifier);
            final User user = ess.getUser(id);

            if(user == null) {
                throw new UserDoesNotExistException(identifier);
            }
            return user;
        } catch(Exception ignore) {
            //We aren't using a UUID
        }

        final User user = ess.getUser(identifier);

        if(user == null) {
            throw new UserDoesNotExistException(identifier);
        }
        return user;
    }

    /**
     * @return Whether or not this implementation is enabled.
     */
    @Override
    public boolean enabled() {
        return true;
    }

    /**
     * @return True if this implementation should override other economy implementations.
     */
    @Override
    public boolean force() {
        return false;
    }

    /**
     * @return Whether or not this implementation should have a default Vault implementation.
     */
    @Override
    public boolean vault() {
        return false;
    }

    /**
     * Used to get the plural name of the default currency.
     * @return The plural name of the default currency.
     */
    @Override
    public String currencyDefaultPlural() {
        return ess.getSettings().getCurrencyPlural();
    }

    /**
     * Used to get the singular name of the default currency.
     * @return The plural name of the default currency.
     */
    @Override
    public String currencyDefaultSingular() {
        return ess.getSettings().getCurrencySingular();
    }

    /**
     * Used to get the plural name of the default currency for a world.
     * @param world The world to be used in this check.
     * @return The plural name of the default currency.
     */
    @Override
    public String currencyDefaultPlural(String world) {
        return ess.getSettings().getCurrencyPlural();
    }

    /**
     * Used to get the singular name of the default currency for a world.
     * @param world The world to be used in this check.
     * @return The plural name of the default currency.
     */
    @Override
    public String currencyDefaultSingular(String world) {
        return ess.getSettings().getCurrencySingular();
    }

    /**
     * Checks to see if a name of the currency exists with this name.
     * @param name The name of the name of the currency to search for.
     * @return True if the currency exists, else false.
     */
    @Override
    public boolean hasCurrency(String name) {
        return name.equalsIgnoreCase(ess.getSettings().getCurrencySingular());
    }

    /**
     * Checks to see if a name of the currency exists with this name.
     * @param name The name of the name of the currency to search for.
     * @param world The name of the {@link World} to check for this name of the currency in.
     * @return True if the currency exists, else false.
     */
    @Override
    public boolean hasCurrency(String name, String world) {
        return name.equalsIgnoreCase(ess.getSettings().getCurrencySingular());
    }

    /**
     * Checks to see if an account exists for this identifier. This method should be used for non-player accounts.
     * @param identifier The identifier of the account.
     * @return True if an account exists for this identifier, else false.
     */
    @Override
    public EconomyResponse hasAccountDetail(String identifier) {
        if (Economy.playerExists(identifier)) {
            return GeneralResponse.SUCCESS;
        }

        if(Economy.playerExists(UUID.nameUUIDFromBytes(("NPC:" + StringUtil.safeString(identifier)).getBytes(Charsets.UTF_8)))) {
            return GeneralResponse.SUCCESS;
        }
        return AccountResponse.DOESNT_EXIST;
    }

    /**
     * Checks to see if an account exists for this identifier. This method should be used for player accounts.
     * @param identifier The {@link UUID} of the account.
     * @return True if an account exists for this identifier, else false.
     */
    @Override
    public EconomyResponse hasAccountDetail(UUID identifier) {
        if (Economy.playerExists(identifier)) {
            return GeneralResponse.SUCCESS;
        }
        return AccountResponse.DOESNT_EXIST;
    }

    /**
     * Attempts to create an account for this identifier. This method should be used for non-player accounts.
     * @param identifier The identifier of the account.
     * @return True if an account was created, else false.
     */
    @Override
    public EconomyResponse createAccountDetail(String identifier) {
        if (hasAccount(identifier)) return AccountResponse.ALREADY_EXISTS;
        if (Economy.createNPC(identifier)) {
            return AccountResponse.CREATED;
        }
        return GeneralResponse.FAILED;
    }

    /**
     * Attempts to create an account for this identifier. This method should be used for player accounts.
     * @param identifier The {@link UUID} of the account.
     * @return True if an account was created, else false.
     */
    @Override
    public EconomyResponse createAccountDetail(UUID identifier) {

        if (hasAccount(identifier)) {
            return AccountResponse.ALREADY_EXISTS;
        }

        final Player player = Bukkit.getPlayer(identifier);
        if (player != null) {

            final User user = ess.getUser(player);
            if(user != null) {
                return AccountResponse.CREATED;
            }
        }
        return GeneralResponse.FAILED;
    }

    /**
     * Attempts to delete an account for this identifier. This method should be used for non-player accounts.
     * @param identifier The identifier of the account.
     * @return True if an account was deleted, else false.
     */
    @Override
    public EconomyResponse deleteAccountDetail(String identifier) {
        try {
            Economy.resetBalance(getUser(identifier));
        } catch (UserDoesNotExistException ignore) {
            return AccountResponse.DOESNT_EXIST;
        } catch (NoLoanPermittedException | MaxMoneyException ignore) {
            return GeneralResponse.FAILED;
        }
        return GeneralResponse.SUCCESS;
    }

    /**
     * Attempts to delete an account for this identifier. This method should be used for player accounts.
     * @param identifier The {@link UUID} of the account.
     * @return True if an account was deleted, else false.
     */
    @Override
    public EconomyResponse deleteAccountDetail(UUID identifier) {
        try {
            final User user = ess.getUser(identifier);
            if(user != null) {
                Economy.resetBalance(user);
            } else {
                return AccountResponse.DOESNT_EXIST;
            }
        } catch (NoLoanPermittedException | MaxMoneyException ignore) {
            return GeneralResponse.FAILED;
        }
        return GeneralResponse.SUCCESS;
    }

    /**
     * Determines whether or not a player is able to access this account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param accessor The identifier of the user attempting to access this account.
     * @return Whether or not the player is able to access this account.
     */
    @Override
    public boolean isAccessor(String identifier, String accessor) {
        return identifier.equals(accessor);
    }

    /**
     * Determines whether or not a player is able to access this account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param accessor The identifier of the user attempting to access this account.
     * @return Whether or not the player is able to access this account.
     */
    @Override
    public boolean isAccessor(String identifier, UUID accessor) {
        return isAccessor(identifier, accessor.toString());
    }

    /**
     * Determines whether or not a player is able to access this account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param accessor The identifier of the user attempting to access this account.
     * @return Whether or not the player is able to access this account.
     */
    @Override
    public boolean isAccessor(UUID identifier, String accessor) {
        return isAccessor(identifier.toString(), accessor);
    }

    /**
     * Determines whether or not a player is able to access this account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param accessor The identifier of the user attempting to access this account.
     * @return Whether or not the player is able to access this account.
     */
    @Override
    public boolean isAccessor(UUID identifier, UUID accessor) {
        return isAccessor(identifier.toString(), accessor.toString());
    }

    /**
     * Determines whether or not a player is able to withdraw holdings from this account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param accessor The identifier of the user attempting to access this account.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse canWithdrawDetail(String identifier, String accessor) {
        if (identifier.equals(accessor)) {
            return GeneralResponse.SUCCESS;
        }
        return GeneralResponse.FAILED;
    }

    /**
     * Determines whether or not a player is able to withdraw holdings from this account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param accessor The identifier of the user attempting to access this account.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse canWithdrawDetail(String identifier, UUID accessor) {
        return canWithdrawDetail(identifier, accessor.toString());
    }

    /**
     * Determines whether or not a player is able to withdraw holdings from this account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param accessor The identifier of the user attempting to access this account.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse canWithdrawDetail(UUID identifier, String accessor) {
        return canWithdrawDetail(identifier.toString(), accessor);
    }

    /**
     * Determines whether or not a player is able to withdraw holdings from this account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param accessor The identifier of the user attempting to access this account.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse canWithdrawDetail(UUID identifier, UUID accessor) {
        return canWithdrawDetail(identifier.toString(), accessor.toString());
    }

    /**
     * Determines whether or not a player is able to deposit holdings into this account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param accessor The identifier of the user attempting to access this account.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse canDepositDetail(String identifier, String accessor) {
        if (identifier.equals(accessor)) {
            return GeneralResponse.SUCCESS;
        }
        return GeneralResponse.FAILED;
    }

    /**
     * Determines whether or not a player is able to deposit holdings into this account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param accessor The identifier of the user attempting to access this account.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse canDepositDetail(String identifier, UUID accessor) {
        return canDepositDetail(identifier, accessor.toString());
    }

    /**
     * Determines whether or not a player is able to deposit holdings into this account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param accessor The identifier of the user attempting to access this account.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse canDepositDetail(UUID identifier, String accessor) {
        return canDepositDetail(identifier.toString(), accessor);
    }

    /**
     * Determines whether or not a player is able to deposit holdings into this account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param accessor The identifier of the user attempting to access this account.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse canDepositDetail(UUID identifier, UUID accessor) {
        return canDepositDetail(identifier.toString(), accessor.toString());
    }

    /**
     * Used to get the balance of an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @return The balance of the account.
     */
    @Override
    public BigDecimal getHoldings(String identifier) {
        if (hasAccount(identifier)) {
            try {

                return Economy.getMoneyExact(getUser(identifier));
            } catch (UserDoesNotExistException ignore) { }
        }
        return ess.getSettings().getStartingBalance();
    }

    /**
     * Used to get the balance of an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @return The balance of the account.
     */
    @Override
    public BigDecimal getHoldings(UUID identifier) {
        if (hasAccount(identifier)) {
            try {
                return Economy.getMoneyExact(identifier);
            } catch (UserDoesNotExistException ignore) { }
        }
        return ess.getSettings().getStartingBalance();
    }

    /**
     * Used to get the balance of an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param world The name of the {@link World} associated with the balance.
     * @return The balance of the account.
     */
    @Override
    public BigDecimal getHoldings(String identifier, String world) {
        return getHoldings(identifier);
    }

    /**
     * Used to get the balance of an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param world The name of the {@link World} associated with the balance.
     * @return The balance of the account.
     */
    @Override
    public BigDecimal getHoldings(UUID identifier, String world) {
        return getHoldings(identifier);
    }

    /**
     * Used to get the balance of an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param world The name of the {@link World} associated with the balance.
     * @param currency The name of the currency associated with the balance.
     * @return The balance of the account.
     */
    @Override
    public BigDecimal getHoldings(String identifier, String world, String currency) {
        return getHoldings(identifier);
    }

    /**
     * Used to get the balance of an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param world The name of the {@link World} associated with the balance.
     * @param currency The name of the currency associated with the balance.
     * @return The balance of the account.
     */
    @Override
    public BigDecimal getHoldings(UUID identifier, String world, String currency) {
        return getHoldings(identifier);
    }

    /**
     * Used to determine if an account has at least an amount of funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to use for this check.
     * @return True if the account has at least the specified amount of funds, otherwise false.
     */
    @Override
    public boolean hasHoldings(String identifier, BigDecimal amount) {
        try {
            return Economy.hasEnough(getUser(identifier), amount);
        } catch (UserDoesNotExistException ignore) {
            return false;
        }
    }

    /**
     * Used to determine if an account has at least an amount of funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to use for this check.
     * @return True if the account has at least the specified amount of funds, otherwise false.
     */
    @Override
    public boolean hasHoldings(UUID identifier, BigDecimal amount) {
        try {
            return Economy.hasEnough(identifier, amount);
        } catch (UserDoesNotExistException ignore) {
            return false;
        }
    }

    /**
     * Used to determine if an account has at least an amount of funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to use for this check.
     * @param world The name of the {@link World} associated with the amount.
     * @return True if the account has at least the specified amount of funds, otherwise false.
     */
    @Override
    public boolean hasHoldings(String identifier, BigDecimal amount, String world) {
        return hasHoldings(identifier, amount);
    }

    /**
     * Used to determine if an account has at least an amount of funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to use for this check.
     * @param world The name of the {@link World} associated with the amount.
     * @return True if the account has at least the specified amount of funds, otherwise false.
     */
    @Override
    public boolean hasHoldings(UUID identifier, BigDecimal amount, String world) {
        return hasHoldings(identifier, amount);
    }

    /**
     * Used to determine if an account has at least an amount of funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to use for this check.
     * @param world The name of the {@link World} associated with the amount.
     * @param currency The name of the currency associated with the balance.
     * @return True if the account has at least the specified amount of funds, otherwise false.
     */
    @Override
    public boolean hasHoldings(String identifier, BigDecimal amount, String world, String currency) {
        return hasHoldings(identifier, amount);
    }

    /**
     * Used to determine if an account has at least an amount of funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to use for this check.
     * @param world The name of the {@link World} associated with the amount.
     * @param currency The name of the currency associated with the balance.
     * @return True if the account has at least the specified amount of funds, otherwise false.
     */
    @Override
    public boolean hasHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
        return hasHoldings(identifier, amount);
    }

    /**
     * Used to set the funds to an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to set this accounts's funds to.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse setHoldingsDetail(String identifier, BigDecimal amount) {
        if (!hasAccount(identifier) && !createAccount(identifier)) {
            return AccountResponse.CREATION_FAILED;
        }

        try {
            Economy.setMoney(getUser(identifier), amount);
            return GeneralResponse.SUCCESS;
        } catch (UserDoesNotExistException ignore) {
            return AccountResponse.DOESNT_EXIST;
        } catch (NoLoanPermittedException | MaxMoneyException ignore) {
            return GeneralResponse.FAILED;
        }
    }

    /**
     * Used to set the funds to an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to set this accounts's funds to.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse setHoldingsDetail(UUID identifier, BigDecimal amount) {
        if (!hasAccount(identifier) && !createAccount(identifier)) {
            return AccountResponse.CREATION_FAILED;
        }
        try {
            Economy.setMoney(identifier, amount);
            return GeneralResponse.SUCCESS;
        } catch (UserDoesNotExistException ignore) {
            return AccountResponse.DOESNT_EXIST;
        } catch (NoLoanPermittedException | MaxMoneyException ignore) {
            return GeneralResponse.FAILED;
        }
    }

    /**
     * Used to set the funds to an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to set this accounts's funds to.
     * @param world The name of the {@link World} associated with the amount.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse setHoldingsDetail(String identifier, BigDecimal amount, String world) {
        return setHoldingsDetail(identifier, amount);
    }

    /**
     * Used to set the funds to an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to set this accounts's funds to.
     * @param world The name of the {@link World} associated with the amount.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse setHoldingsDetail(UUID identifier, BigDecimal amount, String world) {
        return setHoldingsDetail(identifier, amount);
    }

    /**
     * Used to set the funds to an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to set this accounts's funds to.
     * @param world The name of the {@link World} associated with the amount.
     * @param currency The name of the currency associated with the balance.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse setHoldingsDetail(String identifier, BigDecimal amount, String world, String currency) {
        return setHoldingsDetail(identifier, amount);
    }

    /**
     * Used to set the funds to an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to set this accounts's funds to.
     * @param world The name of the {@link World} associated with the amount.
     * @param currency The name of the currency associated with the balance.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse setHoldingsDetail(UUID identifier, BigDecimal amount, String world, String currency) {
        return setHoldingsDetail(identifier, amount);
    }

    /**
     * Used to add funds to an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to add to this account.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse addHoldingsDetail(String identifier, BigDecimal amount) {
        if (!hasAccount(identifier) && !createAccount(identifier)) {
            return AccountResponse.CREATION_FAILED;
        }

        if (getHoldings(identifier).add(amount).compareTo(ess.getSettings().getMaxMoney()) > 0) {
            return HoldingsResponse.MAX_HOLDINGS;
        }

        try {
            Economy.add(getUser(identifier), amount);
            return GeneralResponse.SUCCESS;
        } catch (UserDoesNotExistException ignore) {
            return AccountResponse.DOESNT_EXIST;
        } catch (NoLoanPermittedException | MaxMoneyException ignore) {
            return GeneralResponse.FAILED;
        }
    }

    /**
     * Used to add funds to an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to add to this account.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse addHoldingsDetail(UUID identifier, BigDecimal amount) {
        if (!hasAccount(identifier) && !createAccount(identifier)) {
            return AccountResponse.CREATION_FAILED;
        }

        if (getHoldings(identifier).add(amount).compareTo(ess.getSettings().getMaxMoney()) > 0) {
            return HoldingsResponse.MAX_HOLDINGS;
        }

        try {
            Economy.add(identifier, amount);
            return GeneralResponse.SUCCESS;
        } catch (UserDoesNotExistException ignore) {
            return AccountResponse.DOESNT_EXIST;
        } catch (NoLoanPermittedException | MaxMoneyException ignore) {
            return GeneralResponse.FAILED;
        }
    }

    /**
     * Used to add funds to an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to add to this account.
     * @param world The name of the {@link World} associated with the amount.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse addHoldingsDetail(String identifier, BigDecimal amount, String world) {
        return addHoldingsDetail(identifier, amount);
    }

    /**
     * Used to add funds to an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to add to this account.
     * @param world The name of the {@link World} associated with the amount.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse addHoldingsDetail(UUID identifier, BigDecimal amount, String world) {
        return addHoldingsDetail(identifier, amount);
    }

    /**
     * Used to add funds to an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to add to this account.
     * @param world The name of the {@link World} associated with the amount.
     * @param currency The name of the currency associated with the balance.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse addHoldingsDetail(String identifier, BigDecimal amount, String world, String currency) {
        return addHoldingsDetail(identifier, amount);
    }

    /**
     * Used to add funds to an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to add to this account.
     * @param world The name of the {@link World} associated with the amount.
     * @param currency The name of the currency associated with the balance.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse addHoldingsDetail(UUID identifier, BigDecimal amount, String world, String currency) {
        return addHoldingsDetail(identifier, amount);
    }

    /**
     * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
     * affect an account's funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to add to this account.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse canAddHoldingsDetail(String identifier, BigDecimal amount) {
        if (!hasAccount(identifier) && !createAccount(identifier)) return AccountResponse.CREATION_FAILED;
        if (getHoldings(identifier).add(amount).compareTo(ess.getSettings().getMaxMoney()) > 0) {
            return HoldingsResponse.MAX_HOLDINGS;
        }
        return GeneralResponse.SUCCESS;
    }

    /**
     * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
     * affect an account's funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to add to this account.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse canAddHoldingsDetail(UUID identifier, BigDecimal amount) {
        if (!hasAccount(identifier) && !createAccount(identifier)) return AccountResponse.CREATION_FAILED;
        if (getHoldings(identifier).add(amount).compareTo(ess.getSettings().getMaxMoney()) > 0) {
            return HoldingsResponse.MAX_HOLDINGS;
        }
        return GeneralResponse.SUCCESS;
    }

    /**
     * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
     * affect an account's funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to add to this account.
     * @param world The name of the {@link World} associated with the amount.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse canAddHoldingsDetail(String identifier, BigDecimal amount, String world) {
        return canAddHoldingsDetail(identifier, amount);
    }

    /**
     * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
     * affect an account's funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to add to this account.
     * @param world The name of the {@link World} associated with the amount.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse canAddHoldingsDetail(UUID identifier, BigDecimal amount, String world) {
        return canAddHoldingsDetail(identifier, amount);
    }

    /**
     * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
     * affect an account's funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to add to this account.
     * @param world The name of the {@link World} associated with the amount.
     * @param currency The name of the currency associated with the balance.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse canAddHoldingsDetail(String identifier, BigDecimal amount, String world, String currency) {
        return canAddHoldingsDetail(identifier, amount);
    }

    /**
     * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
     * affect an account's funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to add to this account.
     * @param world The name of the {@link World} associated with the amount.
     * @param currency The name of the currency associated with the balance.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse canAddHoldingsDetail(UUID identifier, BigDecimal amount, String world, String currency) {
        return canAddHoldingsDetail(identifier, amount);
    }


    /**
     * Used to remove funds from an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to remove from this account.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse removeHoldingsDetail(String identifier, BigDecimal amount) {
        if (!hasAccount(identifier) && !createAccount(identifier)) {
            return AccountResponse.CREATION_FAILED;
        }

        if (getHoldings(identifier).subtract(amount).compareTo(ess.getSettings().getMinMoney()) < 0) {
            return HoldingsResponse.MIN_HOLDINGS;
        }

        try {
            Economy.subtract(getUser(identifier), amount);
            return GeneralResponse.SUCCESS;
        } catch (UserDoesNotExistException ignore) {
            return AccountResponse.DOESNT_EXIST;
        } catch (NoLoanPermittedException | MaxMoneyException ignore) {
            return GeneralResponse.FAILED;
        }
    }

    /**
     * Used to remove funds from an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to remove from this account.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse removeHoldingsDetail(UUID identifier, BigDecimal amount) {
        if (!hasAccount(identifier) && !createAccount(identifier)) {
            return AccountResponse.CREATION_FAILED;
        }

        if (getHoldings(identifier).subtract(amount).compareTo(ess.getSettings().getMinMoney()) < 0) {
            return HoldingsResponse.MIN_HOLDINGS;
        }

        try {
            Economy.subtract(identifier, amount);
            return GeneralResponse.SUCCESS;
        } catch (UserDoesNotExistException ignore) {
            return AccountResponse.DOESNT_EXIST;
        } catch (NoLoanPermittedException | MaxMoneyException ignore) {
            return GeneralResponse.FAILED;
        }
    }

    /**
     * Used to remove funds from an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to remove from this account.
     * @param world The name of the {@link World} associated with the amount.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse removeHoldingsDetail(String identifier, BigDecimal amount, String world) {
        return removeHoldingsDetail(identifier, amount);
    }

    /**
     * Used to remove funds from an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to remove from this account.
     * @param world The name of the {@link World} associated with the amount.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse removeHoldingsDetail(UUID identifier, BigDecimal amount, String world) {
        return removeHoldingsDetail(identifier, amount);
    }

    /**
     * Used to remove funds from an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to remove from this account.
     * @param world The name of the {@link World} associated with the amount.
     * @param currency The name of the currency associated with the balance.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse removeHoldingsDetail(String identifier, BigDecimal amount, String world, String currency) {
        return removeHoldingsDetail(identifier, amount);
    }

    /**
     * Used to remove funds from an account.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to remove from this account.
     * @param world The name of the {@link World} associated with the amount.
     * @param currency The name of the currency associated with the balance.
     * @return The {@link EconomyResponse} for this action.
     */
    @Override
    public EconomyResponse removeHoldingsDetail(UUID identifier, BigDecimal amount, String world, String currency) {
        return removeHoldingsDetail(identifier, amount);
    }

    /**
     * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
     * affect an account's funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to remove from this account.
     * @return The {@link EconomyResponse} that would be returned with the corresponding removeHoldingsDetail method.
     */
    @Override
    public EconomyResponse canRemoveHoldingsDetail(String identifier, BigDecimal amount) {
        if (!hasAccount(identifier) && !createAccount(identifier)) return AccountResponse.CREATION_FAILED;
        if (getHoldings(identifier).subtract(amount).compareTo(ess.getSettings().getMinMoney()) < 0) {
            return HoldingsResponse.MIN_HOLDINGS;
        }
        return GeneralResponse.SUCCESS;
    }

    /**
     * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
     * affect an account's funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to remove from this account.
     * @return The {@link EconomyResponse} that would be returned with the corresponding removeHoldingsDetail method.
     */
    @Override
    public EconomyResponse canRemoveHoldingsDetail(UUID identifier, BigDecimal amount) {
        if (!hasAccount(identifier) && !createAccount(identifier)) return AccountResponse.CREATION_FAILED;
        if (getHoldings(identifier).subtract(amount).compareTo(ess.getSettings().getMinMoney()) < 0) {
            return HoldingsResponse.MIN_HOLDINGS;
        }
        return GeneralResponse.SUCCESS;
    }

    /**
     * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
     * affect an account's funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to remove from this account.
     * @param world The name of the {@link World} associated with the amount.
     * @return The {@link EconomyResponse} that would be returned with the corresponding removeHoldingsDetail method.
     */
    @Override
    public EconomyResponse canRemoveHoldingsDetail(String identifier, BigDecimal amount, String world) {
        return canRemoveHoldingsDetail(identifier, amount);
    }

    /**
     * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
     * affect an account's funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to remove from this account.
     * @param world The name of the {@link World} associated with the amount.
     * @return The {@link EconomyResponse} that would be returned with the corresponding removeHoldingsDetail method.
     */
    @Override
    public EconomyResponse canRemoveHoldingsDetail(UUID identifier, BigDecimal amount, String world) {
        return canRemoveHoldingsDetail(identifier, amount);
    }

    /**
     * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
     * affect an account's funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to remove from this account.
     * @param world The name of the {@link World} associated with the amount.
     * @param currency The name of the currency associated with the balance.
     * @return The {@link EconomyResponse} that would be returned with the corresponding removeHoldingsDetail method.
     */
    @Override
    public EconomyResponse canRemoveHoldingsDetail(String identifier, BigDecimal amount, String world, String currency) {
        return canRemoveHoldingsDetail(identifier, amount);
    }

    /**
     * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
     * affect an account's funds.
     * @param identifier The identifier of the account that is associated with this call.
     * @param amount The amount you wish to remove from this account.
     * @param world The name of the {@link World} associated with the amount.
     * @param currency The name of the currency associated with the balance.
     * @return The {@link EconomyResponse} that would be returned with the corresponding removeHoldingsDetail method.
     */
    @Override
    public EconomyResponse canRemoveHoldingsDetail(UUID identifier, BigDecimal amount, String world, String currency) {
        return canRemoveHoldingsDetail(identifier, amount);
    }

    /**
     * Formats a monetary amount into a more text-friendly version.
     * @param amount The amount of currency to format.
     * @return The formatted amount.
     */
    @Override
    public String format(BigDecimal amount) {
        return Economy.format(amount);
    }

    /**
     * Formats a monetary amount into a more text-friendly version.
     * @param amount The amount of currency to format.
     * @param world The {@link World} in which this format operation is occurring.
     * @return The formatted amount.
     */
    @Override
    public String format(BigDecimal amount, String world) {
        return Economy.format(amount);
    }

    /**
     * Formats a monetary amount into a more text-friendly version.
     * @param amount The amount of currency to format.
     * @param world The {@link World} in which this format operation is occurring.
     * @param currency The name of the currency associated with the balance.
     * @return The formatted amount.
     */
    @Override
    public String format(BigDecimal amount, String world, String currency) {
        return Economy.format(amount);
    }

    /**
     * Purges the database of accounts with the default balance.
     * @return True if the purge was completed successfully.
     */
    @Override
    public boolean purgeAccounts() {
        return false;
    }

    /**
     * Purges the database of accounts with a balance under the specified one.
     * @param amount The amount that an account's balance has to be under in order to be removed.
     * @return True if the purge was completed successfully.
     */
    @Override
    public boolean purgeAccountsUnder(BigDecimal amount) {
        return false;
    }
}
