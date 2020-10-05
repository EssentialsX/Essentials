package com.earth2me.essentials.register.payment.methods;

import com.earth2me.essentials.register.payment.Method;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEco implements Method {
    private Plugin vault;
    private Economy economy;

    @Override
    public Plugin getPlugin() {
        return this.vault;
    }

    @Override
    public void setPlugin(final Plugin plugin) {
        this.vault = plugin;
        final RegisteredServiceProvider<Economy> economyProvider = this.vault.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            this.economy = economyProvider.getProvider();
        }
    }

    @Override
    public boolean createAccount(final String name, final Double amount) {
        if (hasAccount(name)) {
            return false;
        }

        return false;
    }

    @Override
    public String getName() {
        return this.vault.getDescription().getName();
    }

    @Override
    public String getBackend() {
        return economy == null ? "NoEco" : economy.getName();
    }

    @Override
    public String getLongName() {
        return getName().concat(" - Economy: ").concat(getBackend());
    }

    @Override
    public String getVersion() {
        return this.vault.getDescription().getVersion();
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(final double amount) {
        return this.economy.format(amount);
    }

    @Override
    public boolean hasBanks() {
        return this.economy.hasBankSupport();
    }

    @Override
    public boolean hasBank(final String bank) {
        return this.economy.getBanks().contains(bank);
    }

    @Override
    public boolean hasAccount(final String name) {
        return this.economy.hasAccount(name);
    }

    @Override
    public boolean hasBankAccount(final String bank, final String name) {
        return this.economy.isBankOwner(bank, name).transactionSuccess() || this.economy.isBankMember(bank, name).transactionSuccess();
    }

    @Override
    public boolean createAccount(final String name) {
        return this.economy.createBank(name, "").transactionSuccess();
    }

    public boolean createAccount(final String name, final double balance) {
        if (!this.economy.createBank(name, "").transactionSuccess()) {
            return false;
        }
        return this.economy.bankDeposit(name, balance).transactionSuccess();
    }

    @Override
    public MethodAccount getAccount(final String name) {
        if (!hasAccount(name)) {
            return null;
        }

        return new VaultAccount(name, this.economy);
    }

    @Override
    public MethodBankAccount getBankAccount(final String bank, final String name) {
        if (!hasBankAccount(bank, name)) {
            return null;
        }

        return new VaultBankAccount(bank, economy);
    }

    @Override
    public boolean isCompatible(final Plugin plugin) {
        try {
            final RegisteredServiceProvider<Economy> ecoPlugin = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            return plugin.getName().equals("Vault") && ecoPlugin != null && !ecoPlugin.getProvider().getName().equals("Essentials Economy");
        } catch (final LinkageError | Exception e) {
            return false;
        }
    }

    public static class VaultAccount implements MethodAccount {
        private final String name;
        private final Economy economy;

        VaultAccount(final String name, final Economy economy) {
            this.name = name;
            this.economy = economy;
        }

        @Override
        public double balance() {
            return this.economy.getBalance(this.name);
        }

        @Override
        public boolean set(final double amount) {
            if (!this.economy.withdrawPlayer(this.name, this.balance()).transactionSuccess()) {
                return false;
            }
            if (amount == 0) {
                return true;
            }
            return this.economy.depositPlayer(this.name, amount).transactionSuccess();
        }

        @Override
        public boolean add(final double amount) {
            return this.economy.depositPlayer(this.name, amount).transactionSuccess();
        }

        @Override
        public boolean subtract(final double amount) {
            return this.economy.withdrawPlayer(this.name, amount).transactionSuccess();
        }

        @Override
        public boolean multiply(final double amount) {
            final double balance = this.balance();
            return this.set(balance * amount);
        }

        @Override
        public boolean divide(final double amount) {
            final double balance = this.balance();
            return this.set(balance / amount);
        }

        @Override
        public boolean hasEnough(final double amount) {
            return this.balance() >= amount;
        }

        @Override
        public boolean hasOver(final double amount) {
            return this.balance() > amount;
        }

        @Override
        public boolean hasUnder(final double amount) {
            return this.balance() < amount;
        }

        @Override
        public boolean isNegative() {
            return this.balance() < 0;
        }

        @Override
        public boolean remove() {
            return this.set(0.0);
        }
    }

    public static class VaultBankAccount implements MethodBankAccount {
        private final String bank;
        private final Economy economy;

        public VaultBankAccount(final String bank, final Economy economy) {
            this.bank = bank;
            this.economy = economy;
        }

        @Override
        public String getBankName() {
            return this.bank;
        }

        @Override
        public int getBankId() {
            return -1;
        }

        @Override
        public double balance() {
            return this.economy.bankBalance(this.bank).balance;
        }

        @Override
        public boolean set(final double amount) {
            if (!this.economy.bankWithdraw(this.bank, this.balance()).transactionSuccess()) {
                return false;
            }
            if (amount == 0) {
                return true;
            }
            return this.economy.bankDeposit(this.bank, amount).transactionSuccess();
        }

        @Override
        public boolean add(final double amount) {
            return this.economy.bankDeposit(this.bank, amount).transactionSuccess();
        }

        @Override
        public boolean subtract(final double amount) {
            return this.economy.bankWithdraw(this.bank, amount).transactionSuccess();
        }

        @Override
        public boolean multiply(final double amount) {
            final double balance = this.balance();
            return this.set(balance * amount);
        }

        @Override
        public boolean divide(final double amount) {
            final double balance = this.balance();
            return this.set(balance / amount);
        }

        @Override
        public boolean hasEnough(final double amount) {
            return this.balance() >= amount;
        }

        @Override
        public boolean hasOver(final double amount) {
            return this.balance() > amount;
        }

        @Override
        public boolean hasUnder(final double amount) {
            return this.balance() < amount;
        }

        @Override
        public boolean isNegative() {
            return this.balance() < 0;
        }

        @Override
        public boolean remove() {
            return this.set(0.0);
        }
    }
}
