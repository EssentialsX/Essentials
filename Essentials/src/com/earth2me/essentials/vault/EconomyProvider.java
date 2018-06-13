package com.earth2me.essentials.vault;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;
import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import net.ess3.api.MaxMoneyException;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EconomyProvider implements Economy {
    private IEssentials ess = null;

    public EconomyProvider(IEssentials ess) {
        if (!ess.getSettings().isEcoDisabled()) {
            this.ess = ess;
            this.ess.getLogger().info("Hooked into Vault!");
        }
    }

    private OfflinePlayer getOfflineFromName(String name) {
        return ess.getServer().getOfflinePlayer(name);
    }

    private EconomyResponse getBankNotSupportedResponse() {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Bank accounts are not supported by EssentialsX Economy.");
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
                    ResponseType.FAILURE,
                    "Cannot withdraw a negative amount.");
        }

        final BigDecimal amount = BigDecimal.valueOf(amountDouble);

        if (user.canAfford(amount)) {
            final BigDecimal bal = user.getMoney();
            user.takeMoney(amount);
            return new EconomyResponse(bal.subtract(user.getMoney()).doubleValue(),
                    user.getMoney().doubleValue(),
                    ResponseType.SUCCESS,
                    null);
        }

        return new EconomyResponse(0,
                user.getMoney().doubleValue(),
                ResponseType.FAILURE,
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
        final User user = ess.getUser(player.getUniqueId());

        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit negative funds.");
        }
        
        try {
            final BigDecimal bal = user.getMoney();
            user.giveMoney(new BigDecimal(amount));
            return new EconomyResponse(user.getMoney().subtract(bal).doubleValue(),
                    user.getMoney().doubleValue(),
                    ResponseType.SUCCESS,
                    null);
		} catch (MaxMoneyException e) {
            return new EconomyResponse(0,
                user.getMoney().doubleValue(),
                ResponseType.FAILURE,
                "Could not deposit fund over maximum limit.");
		}
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

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return getBankNotSupportedResponse();
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return getBankNotSupportedResponse();
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return getBankNotSupportedResponse();
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return getBankNotSupportedResponse();
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return getBankNotSupportedResponse();
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return getBankNotSupportedResponse();
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return getBankNotSupportedResponse();
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return getBankNotSupportedResponse();
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return getBankNotSupportedResponse();
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return getBankNotSupportedResponse();
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return getBankNotSupportedResponse();
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<String>();
    }

    @Deprecated
    @Override
    public boolean createPlayerAccount(String player) {
        return createPlayerAccount(getOfflineFromName(player));
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String player, String world) {
        return createPlayerAccount(getOfflineFromName(player), world);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String world) {
        return createPlayerAccount(player);
    }
}
