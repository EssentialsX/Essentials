package com.earth2me.essentials;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import net.ess3.api.Economy;
import net.tnemc.core.Reserve;
import net.tnemc.core.economy.EconomyAPI;
import net.tnemc.core.economy.currency.Currency;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * @author creatorfromhell <daniel.viddy@gmail.com>
 */
public class Reserve_Essentials implements EconomyAPI {

  private Essentials plugin;

  public Reserve_Essentials(Essentials plugin) {
    this.plugin = plugin;
  }

  public static void register(Essentials plugin) {
    //Check to see if there is an economy provider already registered, if so Essentials will take the back seat.
    if(!((Reserve)Bukkit.getServer().getPluginManager().getPlugin("Reserve")).economyProvided()) {
      Reserve.instance().registerProvider(new Reserve_Essentials(plugin));
    }
  }

  /**
   * @return The name of the Economy implementation.
   */
  @Override
  public String name() {
    return "Essentials Economy";
  }

  /**
   * @return The version of Reserve the Economy implementation supports.
   */
  @Override
  public String version() {
    return "0.1.2.0";
  }

  //This is our method to convert UUID -> username for use with Essentials' create account methods.
  private String getName(UUID identifier) {
    final User user = plugin.getUser(identifier);
    return ((user == null)? identifier.toString() : user.getName());
  }

  /**
   * @return Whether or not this implementation is enabled.
   */
  @Override
  public boolean enabled() {
    return true;
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
    return "Dollars";
  }

  /**
   * Used to get the singular name of the default currency.
   * @return The plural name of the default currency.
   */
  @Override
  public String currencyDefaultSingular() {
    return "Dollar";
  }

  /**
   * Used to get the plural name of the default currency for a world.
   * @param world The world to be used in this check.
   * @return The plural name of the default currency.
   */
  @Override
  public String currencyDefaultPlural(String world) {
    return "Dollars";
  }

  /**
   * Used to get the singular name of the default currency for a world.
   * @param world The world to be used in this check.
   * @return The plural name of the default currency.
   */
  @Override
  public String currencyDefaultSingular(String world) {
    return "Dollar";
  }

  /**
   * Checks to see if a {@link Currency} exists with this name.
   * @param name The name of the {@link Currency} to search for.
   * @return True if the currency exists, else false.
   */
  @Override
  public boolean hasCurrency(String name) {
    return true; //Always return true here as Essentials only supports one currency.
  }

  /**
   * Checks to see if a {@link Currency} exists with this name.
   * @param name The name of the {@link Currency} to search for.
   * @param world The name of the {@link World} to check for this {@link Currency} in.
   * @return True if the currency exists, else false.
   */
  @Override
  public boolean hasCurrency(String name, String world) {
    return true; //Always return true here as Essentials only supports one currency.
  }

  /**
   * Checks to see if a {@link Currency} exists with this name.
   * @param name The name of the {@link Currency} to search for.
   * @return True if the currency exists, else false.
   */
  @Override
  public CompletableFuture<Boolean> asyncHasCurrency(String name) {
    return CompletableFuture.supplyAsync(()->true); //Always return true here as Essentials only supports one currency.
  }

  /**
   * Checks to see if a {@link Currency} exists with this name.
   * @param name The name of the {@link Currency} to search for.
   * @param world The name of the {@link World} to check for this {@link Currency} in.
   * @return True if the currency exists, else false.
   */
  @Override
  public CompletableFuture<Boolean> asyncHasCurrency(String name, String world) {
    return CompletableFuture.supplyAsync(()->true); //Always return true here as Essentials only supports one currency.
  }

  /**
   * Checks to see if an account exists for this identifier. This method should be used for non-player accounts.
   * @param identifier The identifier of the account.
   * @return True if an account exists for this player, else false.
   */
  @Override
  public boolean hasAccount(String identifier) {
    return Economy.playerExists(identifier);
  }

  /**
   * Checks to see if an account exists for this identifier. This method should be used for player accounts.
   * @param identifier The {@link UUID} of the account.
   * @return True if an account exists for this player, else false.
   */
  @Override
  public boolean hasAccount(UUID identifier) {
    return Economy.playerExists(getName(identifier));
  }

  /**
   * Checks to see if an account exists for this identifier. This method should be used for non-player accounts.
   * @param identifier The identifier of the account.
   * @return True if an account exists for this player, else false.
   */
  @Override
  public CompletableFuture<Boolean> asyncHasAccount(String identifier) {
    return CompletableFuture.supplyAsync(new Supplier<Boolean>() {

      /**
       * Gets a result.
       *
       * @return a result
       */
      @Override
      public Boolean get() {
        return Economy.playerExists(identifier);
      }
    });
  }

  /**
   * Checks to see if an account exists for this identifier. This method should be used for player accounts.
   * @param identifier The {@link UUID} of the account.
   * @return True if an account exists for this player, else false.
   */
  @Override
  public CompletableFuture<Boolean> asyncHasAccount(UUID identifier) {
    return CompletableFuture.supplyAsync(new Supplier<Boolean>() {

      /**
       * Gets a result.
       *
       * @return a result
       */
      @Override
      public Boolean get() {
        return Economy.playerExists(getName(identifier));
      }
    });
  }

  /**
   * Attempts to create an account for this identifier. This method should be used for non-player accounts.
   * @param identifier The identifier of the account.
   * @return True if an account was created, else false.
   */
  @Override
  public boolean createAccount(String identifier) {
    if(hasAccount(identifier)) return false;
    return Economy.createNPC(identifier);
  }

  /**
   * Attempts to create an account for this identifier. This method should be used for player accounts.
   * @param identifier The {@link UUID} of the account.
   * @return True if an account was created, else false.
   */
  @Override
  public boolean createAccount(UUID identifier) {
    if(hasAccount(identifier)) return false;
    return Economy.createNPC(getName(identifier));
  }

  /**
   * Attempts to create an account for this identifier. This method should be used for non-player accounts.
   * @param identifier The identifier of the account.
   * @return True if an account was created, else false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCreateAccount(String identifier) {
    return CompletableFuture.supplyAsync(()->{
      if(hasAccount(identifier)) return false;
      return Economy.createNPC(identifier);
    });
  }

  /**
   * Attempts to create an account for this identifier. This method should be used for player accounts.
   * @param identifier The {@link UUID} of the account.
   * @return True if an account was created, else false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCreateAccount(UUID identifier) {
    return CompletableFuture.supplyAsync(()->{
      if(hasAccount(identifier)) return false;
      return Economy.createNPC(getName(identifier));
    });
  }

  /**
   * Attempts to delete an account for this identifier. This method should be used for non-player accounts.
   * @param identifier The identifier of the account.
   * @return True if an account was deleted, else false.
   */
  @Override
  public boolean deleteAccount(String identifier) {
    try {
      Economy.resetBalance(identifier);
    } catch(UserDoesNotExistException | NoLoanPermittedException ignore) {
      return false;
    }
    return true;
  }

  /**
   * Attempts to delete an account for this identifier. This method should be used for player accounts.
   * @param identifier The {@link UUID} of the account.
   * @return True if an account was deleted, else false.
   */
  @Override
  public boolean deleteAccount(UUID identifier) {
    try {
      Economy.resetBalance(getName(identifier));
    } catch(UserDoesNotExistException | NoLoanPermittedException ignore) {
      return false;
    }
    return true;
  }

  /**
   * Attempts to delete an account for this identifier. This method should be used for non-player accounts.
   * @param identifier The identifier of the account.
   * @return True if an account was deleted, else false.
   */
  @Override
  public CompletableFuture<Boolean> asyncDeleteAccount(String identifier) {
    return CompletableFuture.supplyAsync(()->{
      try {
        Economy.resetBalance(identifier);
      } catch(UserDoesNotExistException | NoLoanPermittedException ignore) {
        return false;
      }
      return true;
    });
  }

  /**
   * Attempts to delete an account for this identifier. This method should be used for player accounts.
   * @param identifier The {@link UUID} of the account.
   * @return True if an account was deleted, else false.
   */
  @Override
  public CompletableFuture<Boolean> asyncDeleteAccount(UUID identifier) {
    return CompletableFuture.supplyAsync(()->{
      try {
        Economy.resetBalance(getName(identifier));
      } catch(UserDoesNotExistException | NoLoanPermittedException ignore) {
        return false;
      }
      return true;
    });
  }

  /**
   * Determines whether or not a player is able to access this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to access this account.
   */
  @Override
  public boolean isAccessor(String identifier, String accessor) {
    return false;
  }

  /**
   * Determines whether or not a player is able to access this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to access this account.
   */
  @Override
  public boolean isAccessor(String identifier, UUID accessor) {
    return false;
  }

  /**
   * Determines whether or not a player is able to access this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to access this account.
   */
  @Override
  public boolean isAccessor(UUID identifier, String accessor) {
    return false;
  }

  /**
   * Determines whether or not a player is able to access this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to access this account.
   */
  @Override
  public boolean isAccessor(UUID identifier, UUID accessor) {
    return false;
  }

  /**
   * Determines whether or not a player is able to withdraw holdings from this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to withdraw holdings from this account.
   */
  @Override
  public boolean canWithdraw(String identifier, String accessor) {
    return false;
  }

  /**
   * Determines whether or not a player is able to withdraw holdings from this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to withdraw holdings from this account.
   */
  @Override
  public boolean canWithdraw(String identifier, UUID accessor) {
    return false;
  }

  /**
   * Determines whether or not a player is able to withdraw holdings from this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to withdraw holdings from this account.
   */
  @Override
  public boolean canWithdraw(UUID identifier, String accessor) {
    return false;
  }

  /**
   * Determines whether or not a player is able to withdraw holdings from this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to withdraw holdings from this account.
   */
  @Override
  public boolean canWithdraw(UUID identifier, UUID accessor) {
    return false;
  }

  /**
   * Determines whether or not a player is able to withdraw holdings from this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to withdraw holdings from this account.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanWithdraw(String identifier, String accessor) {
    return CompletableFuture.supplyAsync(()->false);
  }

  /**
   * Determines whether or not a player is able to withdraw holdings from this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to withdraw holdings from this account.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanWithdraw(String identifier, UUID accessor) {
    return CompletableFuture.supplyAsync(()->false);
  }

  /**
   * Determines whether or not a player is able to withdraw holdings from this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to withdraw holdings from this account.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanWithdraw(UUID identifier, String accessor) {
    return CompletableFuture.supplyAsync(()->false);
  }

  /**
   * Determines whether or not a player is able to withdraw holdings from this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to withdraw holdings from this account.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanWithdraw(UUID identifier, UUID accessor) {
    return CompletableFuture.supplyAsync(()->false);
  }

  /**
   * Determines whether or not a player is able to deposit holdings into this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to deposit holdings into this account.
   */
  @Override
  public boolean canDeposit(String identifier, String accessor) {
    return false;
  }

  /**
   * Determines whether or not a player is able to deposit holdings into this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to deposit holdings into this account.
   */
  @Override
  public boolean canDeposit(String identifier, UUID accessor) {
    return false;
  }

  /**
   * Determines whether or not a player is able to deposit holdings into this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to deposit holdings into this account.
   */
  @Override
  public boolean canDeposit(UUID identifier, String accessor) {
    return false;
  }

  /**
   * Determines whether or not a player is able to deposit holdings into this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to deposit holdings into this account.
   */
  @Override
  public boolean canDeposit(UUID identifier, UUID accessor) {
    return false;
  }

  /**
   * Determines whether or not a player is able to deposit holdings into this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to deposit holdings into this account.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanDeposit(String identifier, String accessor) {
    return CompletableFuture.supplyAsync(()->false);
  }

  /**
   * Determines whether or not a player is able to deposit holdings into this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to deposit holdings into this account.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanDeposit(String identifier, UUID accessor) {
    return CompletableFuture.supplyAsync(()->false);
  }

  /**
   * Determines whether or not a player is able to deposit holdings into this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to deposit holdings into this account.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanDeposit(UUID identifier, String accessor) {
    return CompletableFuture.supplyAsync(()->false);
  }

  /**
   * Determines whether or not a player is able to deposit holdings into this account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param accessor The identifier of the user attempting to access this account.
   * @return Whether or not the player is able to deposit holdings into this account.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanDeposit(UUID identifier, UUID accessor) {
    return CompletableFuture.supplyAsync(()->false);
  }

  /**
   * Used to get the balance of an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @return The balance of the account.
   */
  @Override
  public BigDecimal getHoldings(String identifier) {
    if(hasAccount(identifier)) {
      try {
        return Economy.getMoneyExact(identifier);
      } catch(UserDoesNotExistException ignore) { }
    }
    return plugin.getSettings().getStartingBalance();
  }

  /**
   * Used to get the balance of an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @return The balance of the account.
   */
  @Override
  public BigDecimal getHoldings(UUID identifier) {
    if(hasAccount(identifier)) {
      try {
        return Economy.getMoneyExact(getName(identifier));
      } catch(UserDoesNotExistException ignore) { }
    }
    return plugin.getSettings().getStartingBalance();
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
   * @param currency The {@link Currency} associated with the balance.
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
   * @param currency The {@link Currency} associated with the balance.
   * @return The balance of the account.
   */
  @Override
  public BigDecimal getHoldings(UUID identifier, String world, String currency) {
    return getHoldings(identifier);
  }

  /**
   * Used to get the balance of an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @return The balance of the account.
   */
  @Override
  public CompletableFuture<BigDecimal> asyncGetHoldings(String identifier) {
    return CompletableFuture.supplyAsync(()->getHoldings(identifier));
  }

  /**
   * Used to get the balance of an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @return The balance of the account.
   */
  @Override
  public CompletableFuture<BigDecimal> asyncGetHoldings(UUID identifier) {
    return CompletableFuture.supplyAsync(()->getHoldings(identifier));
  }

  /**
   * Used to get the balance of an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param world The name of the {@link World} associated with the balance.
   * @return The balance of the account.
   */
  @Override
  public CompletableFuture<BigDecimal> asyncGetHoldings(String identifier, String world) {
    return CompletableFuture.supplyAsync(()->getHoldings(identifier));
  }

  /**
   * Used to get the balance of an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param world The name of the {@link World} associated with the balance.
   * @return The balance of the account.
   */
  @Override
  public CompletableFuture<BigDecimal> asyncGetHoldings(UUID identifier, String world) {
    return CompletableFuture.supplyAsync(()->getHoldings(identifier));
  }

  /**
   * Used to get the balance of an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param world The name of the {@link World} associated with the balance.
   * @param currency The {@link Currency} associated with the balance.
   * @return The balance of the account.
   */
  @Override
  public CompletableFuture<BigDecimal> asyncGetHoldings(String identifier, String world, String currency) {
    return CompletableFuture.supplyAsync(()->getHoldings(identifier));
  }

  /**
   * Used to get the balance of an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param world The name of the {@link World} associated with the balance.
   * @param currency The {@link Currency} associated with the balance.
   * @return The balance of the account.
   */
  @Override
  public CompletableFuture<BigDecimal> asyncGetHoldings(UUID identifier, String world, String currency) {
    return CompletableFuture.supplyAsync(()->getHoldings(identifier));
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
      return hasAccount(identifier) && Economy.hasEnough(identifier, amount);
    } catch(UserDoesNotExistException ignore) {
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
      return hasAccount(identifier) && Economy.hasEnough(getName(identifier), amount);
    } catch(UserDoesNotExistException ignore) {
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
   * @param currency The {@link Currency} associated with the balance.
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
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the account has at least the specified amount of funds, otherwise false.
   */
  @Override
  public boolean hasHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
    return hasHoldings(identifier, amount);
  }

  /**
   * Used to determine if an account has at least an amount of funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to use for this check.
   * @return True if the account has at least the specified amount of funds, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncHasHoldings(String identifier, BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->hasHoldings(identifier, amount));
  }

  /**
   * Used to determine if an account has at least an amount of funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to use for this check.
   * @return True if the account has at least the specified amount of funds, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncHasHoldings(UUID identifier, BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->hasHoldings(identifier, amount));
  }

  /**
   * Used to determine if an account has at least an amount of funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to use for this check.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the account has at least the specified amount of funds, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncHasHoldings(String identifier, BigDecimal amount, String world) {
    return CompletableFuture.supplyAsync(()->hasHoldings(identifier, amount));
  }

  /**
   * Used to determine if an account has at least an amount of funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to use for this check.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the account has at least the specified amount of funds, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncHasHoldings(UUID identifier, BigDecimal amount, String world) {
    return CompletableFuture.supplyAsync(()->hasHoldings(identifier, amount));
  }

  /**
   * Used to determine if an account has at least an amount of funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to use for this check.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the account has at least the specified amount of funds, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncHasHoldings(String identifier, BigDecimal amount, String world, String currency) {
    return CompletableFuture.supplyAsync(()->hasHoldings(identifier, amount));
  }

  /**
   * Used to determine if an account has at least an amount of funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to use for this check.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the account has at least the specified amount of funds, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncHasHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
    return CompletableFuture.supplyAsync(()->hasHoldings(identifier, amount));
  }

  /**
   * Used to set funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to set from this account.
   * @return True if the funds were set for the account, otherwise false.
   */
  @Override
  public boolean setHoldings(String identifier, BigDecimal amount) {
    if(!hasAccount(identifier)) return false;
    try {
      Economy.setMoney(identifier, amount);
      return true;
    } catch(UserDoesNotExistException | NoLoanPermittedException ignore) {
      return false;
    }
  }

  /**
   * Used to set funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to set from this account.
   * @return True if the funds were set for the account, otherwise false.
   */
  @Override
  public boolean setHoldings(UUID identifier, BigDecimal amount) {
    if(!hasAccount(identifier)) return false;
    try {
      Economy.setMoney(getName(identifier), amount);
      return true;
    } catch(UserDoesNotExistException | NoLoanPermittedException ignore) {
      return false;
    }
  }

  /**
   * Used to set funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to set from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were set for the account, otherwise false.
   */
  @Override
  public boolean setHoldings(String identifier, BigDecimal amount, String world) {
    return setHoldings(identifier, amount);
  }

  /**
   * Used to set funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to set from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were set for the account, otherwise false.
   */
  @Override
  public boolean setHoldings(UUID identifier, BigDecimal amount, String world) {
    return setHoldings(identifier, amount);
  }

  /**
   * Used to set funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to set from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the funds were set for the account, otherwise false.
   */
  @Override
  public boolean setHoldings(String identifier, BigDecimal amount, String world, String currency) {
    return setHoldings(identifier, amount);
  }

  /**
   * Used to set funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to set from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the funds were set for the account, otherwise false.
   */
  @Override
  public boolean setHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
    return setHoldings(identifier, amount);
  }

  /**
   * Used to set funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to set from this account.
   * @return True if the funds were set for the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncSetHoldings(String identifier, BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->setHoldings(identifier, amount));
  }

  /**
   * Used to set funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to set from this account.
   * @return True if the funds were set for the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncSetHoldings(UUID identifier, BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->setHoldings(identifier, amount));
  }

  /**
   * Used to set funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to set from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were set for the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncSetHoldings(String identifier, BigDecimal amount, String world) {
    return CompletableFuture.supplyAsync(()->setHoldings(identifier, amount));
  }

  /**
   * Used to set funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to set from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were set for the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncSetHoldings(UUID identifier, BigDecimal amount, String world) {
    return CompletableFuture.supplyAsync(()->setHoldings(identifier, amount));
  }

  /**
   * Used to set funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to set from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the funds were set for the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncSetHoldings(String identifier, BigDecimal amount, String world, String currency) {
    return CompletableFuture.supplyAsync(()->setHoldings(identifier, amount));
  }

  /**
   * Used to set funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to set from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the funds were set for the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncSetHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
    return CompletableFuture.supplyAsync(()->setHoldings(identifier, amount));
  }

  /**
   * Used to add funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add from this account.
   * @return True if the funds were added to the account, otherwise false.
   */
  @Override
  public boolean addHoldings(String identifier, BigDecimal amount) {
    if(getHoldings(identifier).add(amount).compareTo(plugin.getSettings().getMaxMoney()) <= 0) {
      try {
        Economy.add(identifier, amount);
      } catch(UserDoesNotExistException | NoLoanPermittedException ignore) {
        return false;
      }
    }
    return false;
  }

  /**
   * Used to add funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add from this account.
   * @return True if the funds were added to the account, otherwise false.
   */
  @Override
  public boolean addHoldings(UUID identifier, BigDecimal amount) {
    if(getHoldings(identifier).add(amount).compareTo(plugin.getSettings().getMaxMoney()) <= 0) {
      try {
        Economy.add(getName(identifier), amount);
      } catch(UserDoesNotExistException | NoLoanPermittedException ignore) {
        return false;
      }
    }
    return false;
  }

  /**
   * Used to add funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were added to the account, otherwise false.
   */
  @Override
  public boolean addHoldings(String identifier, BigDecimal amount, String world) {
    return addHoldings(identifier, amount);
  }

  /**
   * Used to add funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were added to the account, otherwise false.
   */
  @Override
  public boolean addHoldings(UUID identifier, BigDecimal amount, String world) {
    return addHoldings(identifier, amount);
  }

  /**
   * Used to add funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the funds were added to the account, otherwise false.
   */
  @Override
  public boolean addHoldings(String identifier, BigDecimal amount, String world, String currency) {
    return addHoldings(identifier, amount);
  }

  /**
   * Used to add funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the funds were added to the account, otherwise false.
   */
  @Override
  public boolean addHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
    return addHoldings(identifier, amount);
  }

  /**
   * Used to add funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add from this account.
   * @return True if the funds were added to the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncAddHoldings(String identifier, BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->addHoldings(identifier, amount));
  }

  /**
   * Used to add funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add from this account.
   * @return True if the funds were added to the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncAddHoldings(UUID identifier, BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->addHoldings(identifier, amount));
  }

  /**
   * Used to add funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were added to the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncAddHoldings(String identifier, BigDecimal amount, String world) {
    return CompletableFuture.supplyAsync(()->addHoldings(identifier, amount));
  }

  /**
   * Used to add funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were added to the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncAddHoldings(UUID identifier, BigDecimal amount, String world) {
    return CompletableFuture.supplyAsync(()->addHoldings(identifier, amount));
  }

  /**
   * Used to add funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the funds were added to the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncAddHoldings(String identifier, BigDecimal amount, String world, String currency) {
    return CompletableFuture.supplyAsync(()->addHoldings(identifier, amount));
  }

  /**
   * Used to add funds to an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the funds were added to the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncAddHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
    return CompletableFuture.supplyAsync(()->addHoldings(identifier, amount));
  }

  /**
   * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add to this account.
   * @return hasAccount(identifier) if a call to the corresponding addHoldings method would return hasAccount(identifier), otherwise false.
   */
  @Override
  public boolean canAddHoldings(String identifier, BigDecimal amount) {
    return hasAccount(identifier) && getHoldings(identifier).add(amount).compareTo(plugin.getSettings().getMaxMoney()) <= 0;
  }

  /**
   * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add to this account.
   * @return hasAccount(identifier) if a call to the corresponding addHoldings method would return hasAccount(identifier), otherwise false.
   */
  @Override
  public boolean canAddHoldings(UUID identifier, BigDecimal amount) {
    return hasAccount(identifier) && getHoldings(identifier).add(amount).compareTo(plugin.getSettings().getMaxMoney()) <= 0;
  }

  /**
   * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add to this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return hasAccount(identifier) if a call to the corresponding addHoldings method would return hasAccount(identifier), otherwise false.
   */
  @Override
  public boolean canAddHoldings(String identifier, BigDecimal amount, String world) {
    return canAddHoldings(identifier, amount);
  }

  /**
   * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add to this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return canAddHoldings(identifier, amount) if a call to the corresponding addHoldings method would return canAddHoldings(identifier, amount), otherwise false.
   */
  @Override
  public boolean canAddHoldings(UUID identifier, BigDecimal amount, String world) {
    return canAddHoldings(identifier, amount);
  }

  /**
   * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add to this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return canAddHoldings(identifier, amount) if a call to the corresponding addHoldings method would return canAddHoldings(identifier, amount), otherwise false.
   */
  @Override
  public boolean canAddHoldings(String identifier, BigDecimal amount, String world, String currency) {
    return canAddHoldings(identifier, amount);
  }

  /**
   * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add to this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return canAddHoldings(identifier, amount) if a call to the corresponding addHoldings method would return canAddHoldings(identifier, amount), otherwise false.
   */
  @Override
  public boolean canAddHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
    return canAddHoldings(identifier, amount);
  }

  /**
   * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add to this account.
   * @return canAddHoldings(identifier, amount) if a call to the corresponding addHoldings method would return canAddHoldings(identifier, amount), otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanAddHoldings(String identifier, BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->canAddHoldings(identifier, amount));
  }

  /**
   * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add to this account.
   * @return canAddHoldings(identifier, amount) if a call to the corresponding addHoldings method would return canAddHoldings(identifier, amount), otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanAddHoldings(UUID identifier, BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->canAddHoldings(identifier, amount));
  }

  /**
   * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add to this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return canAddHoldings(identifier, amount) if a call to the corresponding addHoldings method would return canAddHoldings(identifier, amount), otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanAddHoldings(String identifier, BigDecimal amount, String world) {
    return CompletableFuture.supplyAsync(()->canAddHoldings(identifier, amount));
  }

  /**
   * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add to this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return canAddHoldings(identifier, amount) if a call to the corresponding addHoldings method would return canAddHoldings(identifier, amount), otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanAddHoldings(UUID identifier, BigDecimal amount, String world) {
    return CompletableFuture.supplyAsync(()->canAddHoldings(identifier, amount));
  }

  /**
   * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add to this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return canAddHoldings(identifier, amount) if a call to the corresponding addHoldings method would return canAddHoldings(identifier, amount), otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanAddHoldings(String identifier, BigDecimal amount, String world, String currency) {
    return CompletableFuture.supplyAsync(()->canAddHoldings(identifier, amount));
  }

  /**
   * Used to determine if a call to the corresponding addHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to add to this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return canAddHoldings(identifier, amount) if a call to the corresponding addHoldings method would return canAddHoldings(identifier, amount), otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanAddHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
    return CompletableFuture.supplyAsync(()->canAddHoldings(identifier, amount));
  }

  /**
   * Used to remove funds from an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @return True if the funds were removed from the account, otherwise false.
   */
  @Override
  public boolean removeHoldings(String identifier, BigDecimal amount) {
    if(getHoldings(identifier).subtract(amount).compareTo(plugin.getSettings().getMinMoney()) >= 0) {
      try {
        Economy.substract(identifier, amount);
      } catch(UserDoesNotExistException | NoLoanPermittedException ignore) {
        return false;
      }
    }
    return false;
  }

  /**
   * Used to remove funds from an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @return True if the funds were removed from the account, otherwise false.
   */
  @Override
  public boolean removeHoldings(UUID identifier, BigDecimal amount) {
    if(getHoldings(identifier).subtract(amount).compareTo(plugin.getSettings().getMinMoney()) >= 0) {
      try {
        Economy.substract(getName(identifier), amount);
      } catch(UserDoesNotExistException | NoLoanPermittedException ignore) {
        return false;
      }
    }
    return false;
  }

  /**
   * Used to remove funds from an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were removed from the account, otherwise false.
   */
  @Override
  public boolean removeHoldings(String identifier, BigDecimal amount, String world) {
    return removeHoldings(identifier, amount);
  }

  /**
   * Used to remove funds from an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were removed from the account, otherwise false.
   */
  @Override
  public boolean removeHoldings(UUID identifier, BigDecimal amount, String world) {
    return removeHoldings(identifier, amount);
  }

  /**
   * Used to remove funds from an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the funds were removed from the account, otherwise false.
   */
  @Override
  public boolean removeHoldings(String identifier, BigDecimal amount, String world, String currency) {
    return removeHoldings(identifier, amount);
  }

  /**
   * Used to remove funds from an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the funds were removed from the account, otherwise false.
   */
  @Override
  public boolean removeHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
    return removeHoldings(identifier, amount);
  }

  /**
   * Used to remove funds from an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @return True if the funds were removed from the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncRemoveHoldings(String identifier, BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->removeHoldings(identifier, amount));
  }

  /**
   * Used to remove funds from an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @return True if the funds were removed from the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncRemoveHoldings(UUID identifier, BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->removeHoldings(identifier, amount));
  }

  /**
   * Used to remove funds from an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were removed from the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncRemoveHoldings(String identifier, BigDecimal amount, String world) {
    return CompletableFuture.supplyAsync(()->removeHoldings(identifier, amount));
  }

  /**
   * Used to remove funds from an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were removed from the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncRemoveHoldings(UUID identifier, BigDecimal amount, String world) {
    return CompletableFuture.supplyAsync(()->removeHoldings(identifier, amount));
  }

  /**
   * Used to remove funds from an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the funds were removed from the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncRemoveHoldings(String identifier, BigDecimal amount, String world, String currency) {
    return CompletableFuture.supplyAsync(()->removeHoldings(identifier, amount));
  }

  /**
   * Used to remove funds from an account.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the funds were removed from the account, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncRemoveHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
    return CompletableFuture.supplyAsync(()->removeHoldings(identifier, amount));
  }

  /**
   * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @return True if a call to the corresponding removeHoldings method would return true, otherwise false.
   */
  @Override
  public boolean canRemoveHoldings(String identifier, BigDecimal amount) {
    return hasAccount(identifier) && getHoldings(identifier).subtract(amount).compareTo(plugin.getSettings().getMinMoney()) >= 0;
  }

  /**
   * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @return True if a call to the corresponding removeHoldings method would return true, otherwise false.
   */
  @Override
  public boolean canRemoveHoldings(UUID identifier, BigDecimal amount) {
    return hasAccount(identifier) && getHoldings(identifier).subtract(amount).compareTo(plugin.getSettings().getMinMoney()) >= 0;
  }

  /**
   * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if a call to the corresponding removeHoldings method would return true, otherwise false.
   */
  @Override
  public boolean canRemoveHoldings(String identifier, BigDecimal amount, String world) {
    return canRemoveHoldings(identifier, amount);
  }

  /**
   * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if a call to the corresponding removeHoldings method would return true, otherwise false.
   */
  @Override
  public boolean canRemoveHoldings(UUID identifier, BigDecimal amount, String world) {
    return canRemoveHoldings(identifier, amount);
  }

  /**
   * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if a call to the corresponding removeHoldings method would return true, otherwise false.
   */
  @Override
  public boolean canRemoveHoldings(String identifier, BigDecimal amount, String world, String currency) {
    return canRemoveHoldings(identifier, amount);
  }

  /**
   * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if a call to the corresponding removeHoldings method would return true, otherwise false.
   */
  @Override
  public boolean canRemoveHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
    return canRemoveHoldings(identifier, amount);
  }

  /**
   * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @return True if a call to the corresponding removeHoldings method would return true, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanRemoveHoldings(String identifier, BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->canRemoveHoldings(identifier, amount));
  }

  /**
   * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @return True if a call to the corresponding removeHoldings method would return true, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanRemoveHoldings(UUID identifier, BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->canRemoveHoldings(identifier, amount));
  }

  /**
   * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if a call to the corresponding removeHoldings method would return true, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanRemoveHoldings(String identifier, BigDecimal amount, String world) {
    return CompletableFuture.supplyAsync(()->canRemoveHoldings(identifier, amount));
  }

  /**
   * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if a call to the corresponding removeHoldings method would return true, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanRemoveHoldings(UUID identifier, BigDecimal amount, String world) {
    return CompletableFuture.supplyAsync(()->canRemoveHoldings(identifier, amount));
  }

  /**
   * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if a call to the corresponding removeHoldings method would return true, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanRemoveHoldings(String identifier, BigDecimal amount, String world, String currency) {
    return CompletableFuture.supplyAsync(()->canRemoveHoldings(identifier, amount));
  }

  /**
   * Used to determine if a call to the corresponding removeHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param identifier The identifier of the account that is associated with this call.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if a call to the corresponding removeHoldings method would return true, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanRemoveHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
    return CompletableFuture.supplyAsync(()->canRemoveHoldings(identifier, amount));
  }

  /**
   * Used to transfer funds from one account to another.
   *
   * @param fromIdentifier The identifier of the account that the holdings will be coming from.
   * @param toIdentifier The identifier of the account that the holdings will be going to.
   * @param amount The amount you wish to remove from this account.
   *
   * @return True if the funds were transferred.
   */
  @Override
  public CompletableFuture<Boolean> asyncTransferHoldings(String fromIdentifier, String toIdentifier, BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->transferHoldings(fromIdentifier, toIdentifier, amount));
  }

  /**
   * Used to transfer funds from one account to another.
   * @param fromIdentifier The identifier of the account that the holdings will be coming from.
   * @param toIdentifier The identifier of the account that the holdings will be going to.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were transferred.
   */
  @Override
  public CompletableFuture<Boolean> asyncTransferHoldings(String fromIdentifier, String toIdentifier, BigDecimal amount, String world) {
    return CompletableFuture.supplyAsync(()->transferHoldings(fromIdentifier, toIdentifier, amount, world));
  }

  /**
   * Used to transfer funds from one account to another.
   * @param fromIdentifier The identifier of the account that the holdings will be coming from.
   * @param toIdentifier The identifier of the account that the holdings will be going to.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the funds were transferred.
   */
  @Override
  public CompletableFuture<Boolean> asyncTransferHoldings(String fromIdentifier, String toIdentifier, BigDecimal amount, String world, String currency) {
    return CompletableFuture.supplyAsync(()->transferHoldings(fromIdentifier, toIdentifier, amount, world, currency));
  }

  /**
   * Used to transfer funds from one account to another.
   * @param fromIdentifier The identifier of the account that the holdings will be coming from.
   * @param toIdentifier The identifier of the account that the holdings will be going to.
   * @param amount The amount you wish to remove from this account.
   * @return True if the funds were transferred.
   */
  @Override
  public CompletableFuture<Boolean> asyncTransferHoldings(UUID fromIdentifier, UUID toIdentifier, BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->transferHoldings(fromIdentifier, toIdentifier, amount));
  }

  /**
   * Used to transfer funds from one account to another.
   * @param fromIdentifier The identifier of the account that the holdings will be coming from.
   * @param toIdentifier The identifier of the account that the holdings will be going to.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if the funds were transferred.
   */
  @Override
  public CompletableFuture<Boolean> asyncTransferHoldings(UUID fromIdentifier, UUID toIdentifier, BigDecimal amount, String world) {
    return CompletableFuture.supplyAsync(()->transferHoldings(fromIdentifier, toIdentifier, amount, world));
  }

  /**
   * Used to transfer funds from one account to another.
   * @param fromIdentifier The identifier of the account that the holdings will be coming from.
   * @param toIdentifier The identifier of the account that the holdings will be going to.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if the funds were transferred.
   */
  @Override
  public CompletableFuture<Boolean> asyncTransferHoldings(UUID fromIdentifier, UUID toIdentifier, BigDecimal amount, String world, String currency) {
    return CompletableFuture.supplyAsync(()->transferHoldings(fromIdentifier, toIdentifier, amount, world, currency));
  }

  /**
   * Used to determine if a call to the corresponding transferHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param fromIdentifier The identifier of the account that the holdings will be coming from.
   * @param toIdentifier The identifier of the account that the holdings will be going to.
   * @param amount The amount you wish to remove from this account.
   * @return True if a call to the corresponding transferHoldings method would return true, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanTransferHoldings(String fromIdentifier, String toIdentifier, BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->canTransferHoldings(fromIdentifier, toIdentifier, amount));
  }

  /**
   * Used to determine if a call to the corresponding transferHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param fromIdentifier The identifier of the account that the holdings will be coming from.
   * @param toIdentifier The identifier of the account that the holdings will be going to.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @return True if a call to the corresponding transferHoldings method would return true, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanTransferHoldings(String fromIdentifier, String toIdentifier, BigDecimal amount, String world) {
    return CompletableFuture.supplyAsync(()->canTransferHoldings(fromIdentifier, toIdentifier, amount, world));
  }

  /**
   * Used to determine if a call to the corresponding transferHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param fromIdentifier The identifier of the account that the holdings will be coming from.
   * @param toIdentifier The identifier of the account that the holdings will be going to.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   * @return True if a call to the corresponding transferHoldings method would return true, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanTransferHoldings(String fromIdentifier, String toIdentifier, BigDecimal amount, String world, String currency) {
    return CompletableFuture.supplyAsync(()->canTransferHoldings(fromIdentifier, toIdentifier, amount, world, currency));
  }

  /**
   * Used to determine if a call to the corresponding transferHoldings method would be successful. This method does not
   * affect an account's funds.
   * @param fromIdentifier The identifier of the account that the holdings will be coming from.
   * @param toIdentifier The identifier of the account that the holdings will be going to.
   * @param amount The amount you wish to remove from this account.
   * @return True if a call to the corresponding transferHoldings method would return true, otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanTransferHoldings(UUID fromIdentifier, UUID toIdentifier, BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->canTransferHoldings(fromIdentifier, toIdentifier, amount));
  }

  /**
   * Used to determine if a call to the corresponding transferHoldings method would be successful.
   * This method does not affect an account's funds.
   *
   * @param fromIdentifier The identifier of the account that the holdings will be coming from.
   * @param toIdentifier The identifier of the account that the holdings will be going to.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   *
   * @return True if a call to the corresponding transferHoldings method would return true,
   * otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanTransferHoldings(UUID fromIdentifier, UUID toIdentifier, BigDecimal amount, String world) {
    return CompletableFuture.supplyAsync(()->canTransferHoldings(fromIdentifier, toIdentifier, amount, world));
  }

  /**
   * Used to determine if a call to the corresponding transferHoldings method would be successful.
   * This method does not affect an account's funds.
   *
   * @param fromIdentifier The identifier of the account that the holdings will be coming from.
   * @param toIdentifier The identifier of the account that the holdings will be going to.
   * @param amount The amount you wish to remove from this account.
   * @param world The name of the {@link World} associated with the amount.
   * @param currency The {@link Currency} associated with the balance.
   *
   * @return True if a call to the corresponding transferHoldings method would return true,
   * otherwise false.
   */
  @Override
  public CompletableFuture<Boolean> asyncCanTransferHoldings(UUID fromIdentifier, UUID toIdentifier, BigDecimal amount, String world, String currency) {
    return CompletableFuture.supplyAsync(()->canTransferHoldings(fromIdentifier, toIdentifier, amount, world, currency));
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
   * @param currency The {@link Currency} associated with the balance.
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

  /**
   * Purges the database of accounts with the default balance.
   * @return True if the purge was completed successfully.
   */
  @Override
  public CompletableFuture<Boolean> asyncPurgeAccounts() {
    return CompletableFuture.supplyAsync(()->false);
  }

  /**
   * Purges the database of accounts with a balance under the specified one.
   * @param amount The amount that an account's balance has to be under in order to be removed.
   * @return True if the purge was completed successfully.
   */
  @Override
  public CompletableFuture<Boolean> asyncPurgeAccountsUnder(BigDecimal amount) {
    return CompletableFuture.supplyAsync(()->false);
  }
}