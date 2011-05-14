package com.earth2me.essentials.register.payment;

import org.bukkit.plugin.Plugin;

public interface Method {
    public Object getPlugin();
    public String getName();
    public String getVersion();
    public String format(double amount);
    public boolean hasBanks();
    public boolean hasBank(String bank);
    public boolean hasAccount(String name);
    public boolean hasBankAccount(String bank, String name);
    public MethodAccount getAccount(String name);
    public MethodBankAccount getBankAccount(String bank, String name);
    public boolean isCompatible(Plugin plugin);
    public void setPlugin(Plugin plugin);

    public interface MethodAccount {
        public double balance();
        public boolean set(double amount);
        public boolean add(double amount);
        public boolean subtract(double amount);
        public boolean multiply(double amount);
        public boolean divide(double amount);
        public boolean hasEnough(double amount);
        public boolean hasOver(double amount);
        public boolean hasUnder(double amount);
        public boolean isNegative();
        public boolean remove();

        @Override
        public String toString();
    }

    public interface MethodBankAccount {
        public double balance();
        public String getBankName();
        public int getBankId();
        public boolean set(double amount);
        public boolean add(double amount);
        public boolean subtract(double amount);
        public boolean multiply(double amount);
        public boolean divide(double amount);
        public boolean hasEnough(double amount);
        public boolean hasOver(double amount);
        public boolean hasUnder(double amount);
        public boolean isNegative();
        public boolean remove();

        @Override
        public String toString();
    }
}
