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
    public boolean createAccount(String name, Double amount) {
        if (hasAccount(name)) {
            return false;
        }

        return false;
    }

    @Override
    public String getName() {
        return this.vault.getDescription().getName();
    }

    public String getEconomy() {
        return economy == null ? "NoEco" : economy.getName();
    }

    @Override
    public String getLongName() {
        return getName().concat(" - Economy: ").concat(getEconomy());
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
    public String format(double amount) {
        return this.economy.format(amount);
    }

    @Override
    public boolean hasBanks() {
        return this.economy.hasBankSupport();
    }

    @Override
    public boolean hasBank(String bank) {
        return this.economy.getBanks().contains(bank);
    }

    @Override
    public boolean hasAccount(String name) {
        return this.economy.hasAccount(name);
    }

    @Override
    public boolean hasBankAccount(String bank, String name) {
        return this.economy.isBankOwner(bank, name).transactionSuccess() || this.economy.isBankMember(bank, name).transactionSuccess();
    }

    @Override
    public boolean createAccount(String name) {
        return this.economy.createBank(name, "").transactionSuccess();
    }

    public boolean createAccount(String name, double balance) {
        if (!this.economy.createBank(name, "").transactionSuccess()) {
            return false;
        }
        return this.economy.bankDeposit(name, balance).transactionSuccess();
    }

    @Override
    public MethodAccount getAccount(String name) {
        if (!hasAccount(name)) {
            return null;
        }

        return new VaultAccount(name, this.economy);
    }

    @Override
    public MethodBankAccount getBankAccount(String bank, String name) {
        if (!hasBankAccount(bank, name)) {
            return null;
        }

        return new VaultBankAccount(bank, economy);
    }

    @Override
    public boolean isCompatible(Plugin plugin) {
        try {
            RegisteredServiceProvider<Economy> ecoPlugin = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            return plugin.getName().equals("Vault") && ecoPlugin != null && !ecoPlugin.getProvider().getName().equals("Essentials Economy");
        } catch (LinkageError | Exception e) {
            return false;
        }
    }

    @Override
    public void setPlugin(Plugin plugin) {
        this.vault = plugin;
        RegisteredServiceProvider<Economy> economyProvider = this.vault.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            this.economy = economyProvider.getProvider();
        }
    }


    public static class VaultAccount implements MethodAccount {
        private final String name;
        private final Economy economy;

        VaultAccount(String name, Economy economy) {
            this.name = name;
            this.economy = economy;
        }

        @Override
        public double balance() {
            return this.economy.getBalance(this.name);
        }

        @Override
        public boolean set(double amount) {
            if (!this.economy.withdrawPlayer(this.name, this.balance()).transactionSuccess()) {
                return false;
            }
            if (amount == 0) {
                return true;
            }
            return this.economy.depositPlayer(this.name, amount).transactionSuccess();
        }

        @Override
        public boolean add(double amount) {
            return this.economy.depositPlayer(this.name, amount).transactionSuccess();
        }

        @Override
        public boolean subtract(double amount) {
            return this.economy.withdrawPlayer(this.name, amount).transactionSuccess();
        }

        @Override
        public boolean multiply(double amount) {
            double balance = this.balance();
            return this.set(balance * amount);
        }

        @Override
        public boolean divide(double amount) {
            double balance = this.balance();
            return this.set(balance / amount);
        }

        @Override
        public boolean hasEnough(double amount) {
            return (this.balance() >= amount);
        }

        @Override
        public boolean hasOver(double amount) {
            return (this.balance() > amount);
        }

        @Override
        public boolean hasUnder(double amount) {
            return (this.balance() < amount);
        }

        @Override
        public boolean isNegative() {
            return (this.balance() < 0);
        }

        @Override
        public boolean remove() {
            return this.set(0.0);
        }
    }


    public static class VaultBankAccount implements MethodBankAccount {
        private final String bank;
        private final Economy economy;

        public VaultBankAccount(String bank, Economy economy) {
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
        public boolean set(double amount) {
            if (!this.economy.bankWithdraw(this.bank, this.balance()).transactionSuccess()) {
                return false;
            }
            if (amount == 0) {
                return true;
            }
            return this.economy.bankDeposit(this.bank, amount).transactionSuccess();
        }

        @Override
        public boolean add(double amount) {
            return this.economy.bankDeposit(this.bank, amount).transactionSuccess();
        }

        @Override
        public boolean subtract(double amount) {
            return this.economy.bankWithdraw(this.bank, amount).transactionSuccess();
        }

        @Override
        public boolean multiply(double amount) {
            double balance = this.balance();
            return this.set(balance * amount);
        }

        @Override
        public boolean divide(double amount) {
            double balance = this.balance();
            return this.set(balance / amount);
        }

        @Override
        public boolean hasEnough(double amount) {
            return (this.balance() >= amount);
        }

        @Override
        public boolean hasOver(double amount) {
            return (this.balance() > amount);
        }

        @Override
        public boolean hasUnder(double amount) {
            return (this.balance() < amount);
        }

        @Override
        public boolean isNegative() {
            return (this.balance() < 0);
        }

        @Override
        public boolean remove() {
            return this.set(0.0);
        }
    }
}