package com.nijikokun.register.payment;

import cosine.boseconomy.BOSEconomy;

public class MethodBOSEconomy implements Method {
    private BOSEconomy BOSEconomy;

    public MethodBOSEconomy(BOSEconomy BOSEconomy) {
        this.BOSEconomy = BOSEconomy;
    }

    public BOSEconomy getPlugin() {
        return this.BOSEconomy;
    }

    public String getName() {
        return "BOSEconomy";
    }

    public String getVersion() {
        return "0.6.2";
    }

    public String format(double amount) {
        String currency = this.BOSEconomy.getMoneyNamePlural();
        if(amount == 1) currency = this.BOSEconomy.getMoneyName();
        return amount + " " + currency;
    }

    public boolean hasBanks() {
        return true;
    }

    public boolean hasBank(String bank) {
        return this.BOSEconomy.bankExists(bank);
    }

    public boolean hasAccount(String name) {
        return this.BOSEconomy.playerRegistered(name, false);
    }

    public boolean hasBankAccount(String bank, String name) {
        return this.BOSEconomy.isBankOwner(bank, name);
    }

    public MethodAccount getAccount(String name) {
        if(!hasAccount(name)) return null;
        return new BOSEAccount(name, this.BOSEconomy);
    }

    public MethodBankAccount getBankAccount(String bank, String name) {
        return new BOSEBankAccount(bank, name, BOSEconomy);
    }

    public class BOSEAccount implements MethodAccount {
        private String name;
        private BOSEconomy BOSEconomy;

        public BOSEAccount(String name, BOSEconomy bOSEconomy) {
            this.name = name;
            this.BOSEconomy = bOSEconomy;
        }

        public double balance() {
            return Double.valueOf(this.BOSEconomy.getPlayerMoney(this.name));
        }

        public boolean add(double amount) {
            int IntAmount = (int)Math.ceil(amount);
            int balance = (int)this.balance();
            return this.BOSEconomy.addPlayerMoney(this.name, IntAmount, false);
        }

        public boolean subtract(double amount) {
            int IntAmount = (int)Math.ceil(amount);
            int balance = (int)this.balance();
            return this.BOSEconomy.setPlayerMoney(this.name, (balance - IntAmount), false);
        }

        public boolean multiply(double amount) {
            int IntAmount = (int)Math.ceil(amount);
            int balance = (int)this.balance();
            return this.BOSEconomy.setPlayerMoney(this.name, (balance * IntAmount), false);
        }

        public boolean divide(double amount) {
            int IntAmount = (int)Math.ceil(amount);
            int balance = (int)this.balance();
            return this.BOSEconomy.setPlayerMoney(this.name, (balance / IntAmount), false);
        }

        public boolean hasEnough(double amount) {
            return (this.balance() >= amount);
        }

        public boolean hasOver(double amount) {
            return (this.balance() > amount);
        }

        public boolean hasUnder(double amount) {
            return (this.balance() < amount);
        }

        public boolean isNegative() {
            return (this.balance() < 0);
        }

        public boolean remove() {
            return false;
        }
    }

    public class BOSEBankAccount implements MethodBankAccount {
        private String bank;
        private String name;
        private BOSEconomy BOSEconomy;

        public BOSEBankAccount(String bank, String name, BOSEconomy bOSEconomy) {
            this.name = name;
            this.bank = bank;
            this.BOSEconomy = bOSEconomy;
        }

        public String getBankName() {
            return this.bank;
        }

        public int getBankId() {
            return -1;
        }

        public double balance() {
            return Double.valueOf(this.BOSEconomy.getBankMoney(name));
        }

        public boolean add(double amount) {
            int IntAmount = (int)Math.ceil(amount);
            int balance = (int)this.balance();
            return this.BOSEconomy.setBankMoney(this.name, (balance + IntAmount), false);
        }

        public boolean subtract(double amount) {
            int IntAmount = (int)Math.ceil(amount);
            int balance = (int)this.balance();
            return this.BOSEconomy.setBankMoney(this.name, (balance - IntAmount), false);
        }

        public boolean multiply(double amount) {
            int IntAmount = (int)Math.ceil(amount);
            int balance = (int)this.balance();
            return this.BOSEconomy.setBankMoney(this.name, (balance * IntAmount), false);
        }

        public boolean divide(double amount) {
            int IntAmount = (int)Math.ceil(amount);
            int balance = (int)this.balance();
            return this.BOSEconomy.setBankMoney(this.name, (balance / IntAmount), false);
        }

        public boolean hasEnough(double amount) {
            return (this.balance() >= amount);
        }

        public boolean hasOver(double amount) {
            return (this.balance() > amount);
        }

        public boolean hasUnder(double amount) {
            return (this.balance() < amount);
        }

        public boolean isNegative() {
            return (this.balance() < 0);
        }

        public boolean remove() {
            return this.BOSEconomy.removeBank(bank);
        }
    }
}