package com.earth2me.essentials.vault;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;
import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.List;

public class EconomyHook implements Economy {
    private IEssentials ess = null;

    public EconomyHook(IEssentials ess) {
        if (!ess.getSettings().isEcoDisabled()) {
            this.ess = ess;
            this.ess.getLogger().info("Hooked into Vault!");
        }
    }

    private OfflinePlayer getOfflineFromName(String name) {
        return ess.getServer().getOfflinePlayer(name);
    }

    @Override
    public boolean isEnabled() {
        return ess != null;
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
        return ess.getSettings().getCurrencySymbol();
    }

    @Override
    public String currencyNameSingular() {
        return ess.getSettings().getCurrencySymbol();
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return ess.getUser(player.getUniqueId()) != null;
    }

    @Deprecated
    @Override
    public boolean hasAccount(String player) {
        return hasAccount(getOfflineFromName(player));
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String world) {
        return hasAccount(player);
    }

    @Deprecated
    @Override
    public boolean hasAccount(String player, String world) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return ess.getUser(player.getUniqueId()).getMoney().doubleValue();
    }

    @Deprecated
    @Override
    public double getBalance(String player) {
        return getBalance(getOfflineFromName(player));
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public double getBalance(String player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return ess.getUser(player.getUniqueId()).canAfford(BigDecimal.valueOf(amount));
    }

    @Deprecated
    @Override
    public boolean has(String player, double amount) {
        return has(getOfflineFromName(player), amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String world, double amount) {
        return has(player, amount);
    }

    @Deprecated
    @Override
    public boolean has(String player, String world, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amountDouble) {
        final User user = ess.getUser(player.getUniqueId());

        if (amountDouble < 0) {
            return new EconomyResponse(0,
                    user.getMoney().doubleValue(),
                    EconomyResponse.ResponseType.FAILURE,
                    "Cannot withdraw a negative amount.");
        }

        final BigDecimal amount = BigDecimal.valueOf(amountDouble);

        if (user.canAfford(amount)) {
            final BigDecimal bal = user.getMoney();
            user.takeMoney(amount);
            return new EconomyResponse(bal.subtract(user.getMoney()).doubleValue(),
                    user.getMoney().doubleValue(),
                    EconomyResponse.ResponseType.SUCCESS,
                    null);
        }

        return new EconomyResponse(0,
                user.getMoney().doubleValue(),
                EconomyResponse.ResponseType.FAILURE,
                "Could not afford withdrawal.");
    }

    @Deprecated
    @Override
    public EconomyResponse withdrawPlayer(String player, double amount) {
        return withdrawPlayer(getOfflineFromName(player), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String world, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Deprecated
    @Override
    public EconomyResponse withdrawPlayer(String player, String world, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return null; // TODO Make this do stuff
    }

    @Deprecated
    @Override
    public EconomyResponse depositPlayer(String player, double amount) {
        return depositPlayer(getOfflineFromName(player), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String world, double amount) {
        return depositPlayer(player, amount);
    }

    @Deprecated
    @Override
    public EconomyResponse depositPlayer(String player, String world, double amount) {
        return depositPlayer(player, amount);
    }

    /**
     * @param s
     * @param s1
     * @deprecated
     */
    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null; // TODO Decide whether to implement banks via createNpc or not
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    /**
     * @param s
     * @param s1
     * @deprecated
     */
    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    /**
     * @param s
     * @param s1
     * @deprecated
     */
    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    /**
     * @param s
     * @deprecated
     */
    @Override
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    /**
     * @param s
     * @param s1
     * @deprecated
     */
    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }
}
