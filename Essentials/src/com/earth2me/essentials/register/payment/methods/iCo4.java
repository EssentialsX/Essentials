package com.earth2me.essentials.register.payment.methods;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;
import com.earth2me.essentials.register.payment.Method;
import com.earth2me.essentials.register.payment.MethodFactory;
import org.bukkit.plugin.Plugin;

public class iCo4 implements Method {
    private iConomy iConomy;

    static {
        MethodFactory.addMethod(new iCo4());
    }

    public iConomy getPlugin() {
        return this.iConomy;
    }

    public String getName() {
        return "iConomy";
    }

    public String getVersion() {
        return "4";
    }

    public String format(double amount) {
        return this.iConomy.getBank().format(amount);
    }

    public boolean hasBanks() {
        return false;
    }

    public boolean hasBank(String bank) {
        return false;
    }

    public boolean hasAccount(String name) {
        return this.iConomy.getBank().hasAccount(name);
    }

    public boolean hasBankAccount(String bank, String name) {
        return false;
    }

    public MethodAccount getAccount(String name) {
        return new iCoAccount(this.iConomy.getBank().getAccount(name));
    }

    public MethodBankAccount getBankAccount(String bank, String name) {
        return null;
    }
	
    public boolean isCompatible(Plugin plugin) {
        return plugin.getDescription().getName().equalsIgnoreCase("iconomy") && plugin instanceof iConomy;
    }

    public void setPlugin(Plugin plugin) {
        iConomy = (iConomy)plugin;
    }
	
    public class iCoAccount implements MethodAccount {
        private Account account;

        public iCoAccount(Account account) {
            this.account = account;
        }

        public Account getiCoAccount() {
            return account;
        }

        public double balance() {
            return this.account.getBalance();
        }

        public boolean set(double amount) {
            if(this.account == null) return false;
            this.account.setBalance(amount);
            return true;
        }

        public boolean add(double amount) {
            if(this.account == null) return false;
            this.account.add(amount);
            return true;
        }

        public boolean subtract(double amount) {
            if(this.account == null) return false;
            this.account.subtract(amount);
            return true;
        }

        public boolean multiply(double amount) {
            if(this.account == null) return false;
            this.account.multiply(amount);
            return true;
        }

        public boolean divide(double amount) {
            if(this.account == null) return false;
            this.account.divide(amount);
            return true;
        }

        public boolean hasEnough(double amount) {
            return this.account.hasEnough(amount);
        }

        public boolean hasOver(double amount) {
            return this.account.hasOver(amount);
        }

        public boolean hasUnder(double amount) {
            return (this.balance() < amount);
        }

        public boolean isNegative() {
            return this.account.isNegative();
        }

        public boolean remove() {
            if(this.account == null) return false;
            this.account.remove();
            return true;
        }
    }
}
