package com.earth2me.essentials.register.payment.methods;


import com.earth2me.essentials.register.payment.Method;
import net.tnemc.core.Reserve;
import net.tnemc.core.economy.EconomyAPI;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;

public class ReserveEco implements Method {

    Plugin plugin;
    EconomyAPI economy;

    /**
     * Encodes the Plugin into an Object disguised as the Plugin. If you want the original Plugin
     * Class you must cast it to the correct Plugin, to do so you have to verify the name and or
     * version then cast.
     * <p/>
     * <pre>
     *    if (method.getName().equalsIgnoreCase("iConomy"))
     *     iConomy plugin = ((iConomy)method.getPlugin());</pre>
     *
     * @return <code>Object</code>
     *
     * @see #getName()
     * @see #getVersion()
     */
    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Returns the actual name of this method.
     *
     * @return <code>String</code> Plugin name.
     */
    @Override
    public String getName() {
        return "Reserve";
    }

    public String getProvider() {
        return economy == null ? "No Provider" : economy.name();
    }

    /**
     * Returns the reported name of this method.
     *
     * @return <code>String</code> Plugin name.
     */
    @Override
    public String getLongName() {
        return "Reserve Economy: " + getProvider();
    }

    /**
     * Returns the actual version of this method.
     *
     * @return <code>String</code> Plugin version.
     */
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    /**
     * Returns the amount of decimal places that get stored NOTE: it will return -1 if there is no
     * rounding
     *
     * @return <code>int</code> for each decimal place
     */
    @Override
    public int fractionalDigits() {
        return -1;
    }

    /**
     * Formats amounts into this payment methods style of currency display.
     *
     * @param amount Double
     *
     * @return <code>String</code> - Formatted Currency Display.
     */
    @Override
    public String format(double amount) {
        return economy.format(BigDecimal.valueOf(amount));
    }

    /**
     * Allows the verification of bank API existence in this payment method.
     *
     * @return <code>boolean</code>
     */
    @Override
    public boolean hasBanks() {
        return false;
    }

    /**
     * Determines the existence of a bank via name.
     *
     * @param bank Bank name
     *
     * @return <code>boolean</code>
     *
     * @see #hasBanks
     */
    @Override
    public boolean hasBank(String bank) {
        return false;
    }

    /**
     * Determines the existence of an account via name.
     *
     * @param name Account name
     *
     * @return <code>boolean</code>
     */
    @Override
    public boolean hasAccount(String name) {
        return economy.hasAccount(name);
    }

    /**
     * Check to see if an account <code>name</code> is tied to a <code>bank</code>.
     *
     * @param bank Bank name
     * @param name Account name
     *
     * @return <code>boolean</code>
     */
    @Override
    public boolean hasBankAccount(String bank, String name) {
        return false;
    }

    /**
     * Forces an account creation
     *
     * @param name Account name
     *
     * @return <code>boolean</code>
     */
    @Override
    public boolean createAccount(String name) {
        return economy.createAccount(name);
    }

    /**
     * Forces an account creation
     *
     * @param name Account name
     * @param balance Initial account balance
     *
     * @return <code>boolean</code>
     */
    @Override
    public boolean createAccount(String name, Double balance) {
        return economy.createAccount(name) && economy.addHoldings(name, BigDecimal.valueOf(balance));
    }

    /**
     * Returns a <code>MethodAccount</code> class for an account <code>name</code>.
     *
     * @param name Account name
     *
     * @return <code>MethodAccount</code> <em>or</em>    <code>Null</code>
     */
    @Override
    public MethodAccount getAccount(String name) {
        if (!hasAccount(name)) return null;
        return new ReserveAccount(name, economy);
    }

    /**
     * Returns a <code>MethodBankAccount</code> class for an account <code>name</code>.
     *
     * @param bank Bank name
     * @param name Account name
     *
     * @return <code>MethodBankAccount</code> <em>or</em>    <code>Null</code>
     */
    @Override
    public MethodBankAccount getBankAccount(String bank, String name) {
        return null;
    }

    /**
     * Checks to verify the compatibility between this Method and a plugin. Internal usage only, for
     * the most part.
     *
     * @param plugin Plugin
     *
     * @return <code>boolean</code>
     */
    @Override
    public boolean isCompatible(Plugin plugin) {
        try {
            EconomyAPI economyAPI = ((Reserve) plugin).economy();
            return plugin.getName().equals("Reserve") && economyAPI != null && !economyAPI.name().equals("EssentialsX");
        } catch (LinkageError | Exception e) {
            return false;
        }
    }

    /**
     * Set Plugin data.
     *
     * @param plugin Plugin
     */
    @Override
    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;

        if (((Reserve) plugin).economyProvided()) {
            this.economy = ((Reserve) plugin).economy();
        }
    }

    public class ReserveAccount implements MethodAccount {

        private String identifier;
        private EconomyAPI economy;

        public ReserveAccount(String identifier, EconomyAPI economy) {
            this.identifier = identifier;
            this.economy = economy;
        }

        @Override
        public double balance() {
            return economy.getHoldings(identifier).doubleValue();
        }

        @Override
        public boolean set(double amount) {
            return economy.setHoldings(identifier, BigDecimal.valueOf(amount));
        }

        @Override
        public boolean add(double amount) {
            return economy.setHoldings(identifier, economy.getHoldings(identifier).add(BigDecimal.valueOf(amount)));
        }

        @Override
        public boolean subtract(double amount) {
            return economy.setHoldings(identifier, economy.getHoldings(identifier).subtract(BigDecimal.valueOf(amount)));
        }

        @Override
        public boolean multiply(double amount) {
            return economy.setHoldings(identifier, BigDecimal.valueOf(economy.getHoldings(identifier).doubleValue() * amount));
        }

        @Override
        public boolean divide(double amount) {
            return economy.setHoldings(identifier, BigDecimal.valueOf(economy.getHoldings(identifier).doubleValue() / amount));
        }

        @Override
        public boolean hasEnough(double amount) {
            return economy.hasHoldings(identifier, BigDecimal.valueOf(amount));
        }

        @Override
        public boolean hasOver(double amount) {
            return economy.getHoldings(identifier).compareTo(BigDecimal.ZERO) > 0;
        }

        @Override
        public boolean hasUnder(double amount) {
            return economy.getHoldings(identifier).compareTo(BigDecimal.ZERO) < 0;
        }

        @Override
        public boolean isNegative() {
            return economy.getHoldings(identifier).compareTo(BigDecimal.ZERO) < 0;
        }

        @Override
        public boolean remove() {
            return economy.deleteAccount(identifier);
        }
    }
}