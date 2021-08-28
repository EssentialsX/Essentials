package com.earth2me.essentials.economy.vault;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.earth2me.essentials.config.EssentialsUserConfiguration;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.base.Charsets;
import net.ess3.api.MaxMoneyException;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A goddamn Vault adapter, what more do you want?
 * Provides access to the EssentialsX economy for plugins that use the Vault API.
 * <p>
 * Developer note: for accessing Essentials/Vault economy functions from EssentialsX code, see
 * {@link com.earth2me.essentials.User}.
 */
public class VaultEconomyProvider implements Economy {
    private static final Logger LOGGER = Logger.getLogger("Essentials");
    private static final String WARN_NPC_RECREATE_1 = "Account creation was requested for NPC user {0}, but an account file with UUID {1} already exists.";
    private static final String WARN_NPC_RECREATE_2 = "Essentials will create a new account as requested by the other plugin, but this is almost certainly a bug and should be reported.";

    private final Essentials ess;

    public VaultEconomyProvider(Essentials essentials) {
        this.ess = essentials;
    }

    @Override
    public boolean isEnabled() {
        return ess.isEnabled();
    }

    @Override
    public String getName() {
        return "EssentialsX Economy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double amount) {
        return NumberUtil.displayCurrency(BigDecimal.valueOf(amount), ess);
    }

    @Override
    public String currencyNamePlural() {
        return currencyNameSingular();
    }

    @Override
    public String currencyNameSingular() {
        return ess.getSettings().getCurrencySymbol();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasAccount(String playerName) {
        if (com.earth2me.essentials.api.Economy.playerExists(playerName)) {
            return true;
        }
        // We may not have the player name in the usermap, let's double check an NPC account with this name doesn't exist.
        return com.earth2me.essentials.api.Economy.playerExists(UUID.nameUUIDFromBytes(("NPC:" + StringUtil.safeString(playerName)).getBytes(Charsets.UTF_8)));
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return com.earth2me.essentials.api.Economy.playerExists(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @SuppressWarnings("deprecation")
    @Override
    public double getBalance(String playerName) {
        try {
            return getDoubleValue(com.earth2me.essentials.api.Economy.getMoneyExact(playerName));
        } catch (UserDoesNotExistException e) {
            createPlayerAccount(playerName);
            return getDoubleValue(ess.getSettings().getStartingBalance());
        }
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        try {
            return getDoubleValue(com.earth2me.essentials.api.Economy.getMoneyExact(player.getUniqueId()));
        } catch (UserDoesNotExistException e) {
            createPlayerAccount(player);
            return getDoubleValue(ess.getSettings().getStartingBalance());
        }
    }

    private double getDoubleValue(final BigDecimal value) {
        double amount = value.doubleValue();
        if (new BigDecimal(amount).compareTo(value) > 0) {
            // closest double is bigger than the exact amount
            // -> get the previous double value to not return more money than the user has
            amount = Math.nextAfter(amount, Double.NEGATIVE_INFINITY);
        }
        return amount;
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean has(String playerName, double amount) {
        try {
            return com.earth2me.essentials.api.Economy.hasEnough(playerName, amount);
        } catch (UserDoesNotExistException e) {
            return false;
        }
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        try {
            return com.earth2me.essentials.api.Economy.hasEnough(player.getUniqueId(), BigDecimal.valueOf(amount));
        } catch (UserDoesNotExistException e) {
            return false;
        }
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (playerName == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player name cannot be null!");
        }
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds!");
        }

        try {
            com.earth2me.essentials.api.Economy.subtract(playerName, amount);
            return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
        } catch (UserDoesNotExistException e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "User does not exist!");
        } catch (NoLoanPermittedException e) {
            return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Loan was not permitted!");
        } catch (MaxMoneyException e) {
            return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "User goes over maximum money limit!");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player cannot be null!");
        }
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds!");
        }

        try {
            com.earth2me.essentials.api.Economy.subtract(player.getUniqueId(), BigDecimal.valueOf(amount));
            return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null);
        } catch (UserDoesNotExistException e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "User does not exist!");
        } catch (NoLoanPermittedException e) {
            return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Loan was not permitted!");
        } catch (MaxMoneyException e) {
            return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "User goes over maximum money limit!");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        if (playerName == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player name can not be null.");
        }
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");
        }

        try {
            com.earth2me.essentials.api.Economy.add(playerName, amount);
            return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
        } catch (UserDoesNotExistException e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "User does not exist!");
        } catch (NoLoanPermittedException e) {
            return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Loan was not permitted!");
        } catch (MaxMoneyException e) {
            return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "User goes over maximum money limit!");
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player can not be null.");
        }
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");
        }

        try {
            com.earth2me.essentials.api.Economy.add(player.getUniqueId(), BigDecimal.valueOf(amount));
            return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null);
        } catch (UserDoesNotExistException e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "User does not exist!");
        } catch (NoLoanPermittedException e) {
            return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Loan was not permitted!");
        } catch (MaxMoneyException e) {
            return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "User goes over maximum money limit!");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        if (hasAccount(playerName)) {
            return false;
        }
        // Assume we're creating an NPC here? If not, it's a lost cause anyway!
        return com.earth2me.essentials.api.Economy.createNPC(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        if (hasAccount(player)) {
            return false;
        }

        // String based UUIDs are version 3 and are used for NPC and OfflinePlayers
        // Citizens uses v2 UUIDs, yeah I don't know either!
        if (player.getUniqueId().version() == 3 || player.getUniqueId().version() == 2) {
            final File folder = new File(ess.getDataFolder(), "userdata");
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    throw new RuntimeException("Error while creating userdata directory!");
                }
            }
            final File npcFile = new File(folder, player.getUniqueId() + ".yml");
            if (npcFile.exists()) {
                LOGGER.log(Level.SEVERE, MessageFormat.format(WARN_NPC_RECREATE_1, player.getName(), player.getUniqueId().toString()), new RuntimeException());
                LOGGER.log(Level.SEVERE, WARN_NPC_RECREATE_2);
            }
            final EssentialsUserConfiguration npcConfig = new EssentialsUserConfiguration(player.getName(), player.getUniqueId(), npcFile);
            npcConfig.load();
            npcConfig.setProperty("npc", true);
            npcConfig.setProperty("last-account-name", player.getName());
            npcConfig.setProperty("money", ess.getSettings().getStartingBalance());
            npcConfig.blockingSave();
            ess.getUserMap().trackUUID(player.getUniqueId(), player.getName(), false);
            return true;
        }

        // Loading a v4 UUID that we somehow didn't track, mark it as a normal player and hope for the best, vault sucks :/
        try {
            if (ess.getSettings().isDebug()) {
                LOGGER.info("Vault requested a player account creation for a v4 UUID: " + player);
            }
            ess.getUserMap().load(player);
            return true;
        } catch (UserDoesNotExistException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EssentialsX does not support bank accounts!");
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EssentialsX does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EssentialsX does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EssentialsX does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EssentialsX does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EssentialsX does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EssentialsX does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EssentialsX does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EssentialsX does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EssentialsX does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "EssentialsX does not support bank accounts!");
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }
}
