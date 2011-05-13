package com.nijikokun.register.payment;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.api.Economy;

public class MethodEEco implements Method {
    private Essentials Essentials;

    public MethodEEco(Essentials Essentials) {
        this.Essentials = Essentials;
    }

    public Essentials getPlugin() {
        return this.Essentials;
    }

    public String getName() {
        return "EssentialsEco";
    }

    public String getVersion() {
        return "2.2";
    }

    public String format(double amount) {
        return Economy.format(amount);
    }

    public boolean hasBanks() {
        return false;
    }

    public boolean hasBank(String bank) {
        return false;
    }

    public boolean hasAccount(String name) {
        return Economy.accountExist(name);
    }

    public boolean hasBankAccount(String bank, String name) {
        return false;
    }

    public MethodAccount getAccount(String name) {
        if(!hasAccount(name)) return null;
        return new EEcoAccount(name);
    }

    public MethodBankAccount getBankAccount(String bank, String name) {
        return null;
    }

    public class EEcoAccount implements MethodAccount {
        private String name;

        public EEcoAccount(String name) {
            this.name = name;
        }

        public double balance() {
            return Economy.getMoney(this.name);
        }

        public boolean add(double amount) {
            Economy.add(name, amount);
            return true;
        }

        public boolean subtract(double amount) {
            Economy.subtract(name, amount);
            return true;
        }

        public boolean multiply(double amount) {
            Economy.multiply(name, amount);
            return true;
        }

        public boolean divide(double amount) {
            Economy.divide(name, amount);
            return true;
        }

        public boolean hasEnough(double amount) {
            return Economy.hasEnough(name, amount);
        }

        public boolean hasOver(double amount) {
            return Economy.hasMore(name, amount);
        }

        public boolean hasUnder(double amount) {
            return Economy.hasLess(name, amount);
        }

        public boolean isNegative() {
            return Economy.isNegative(name);
        }

        public boolean remove() {
            Economy.removeAccount(name);
            return true;
        }
    }
}