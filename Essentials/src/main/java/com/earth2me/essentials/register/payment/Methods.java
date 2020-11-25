package com.earth2me.essentials.register.payment;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.HashSet;
import java.util.Set;

/**
 * The <code>Methods</code> initializes Methods that utilize the Method interface based on a "first come, first served"
 * basis.
 * <p>
 * Allowing you to check whether a payment method exists or not.
 * <p>
 * Methods also allows you to set a preferred method of payment before it captures payment plugins in the initialization
 * process.
 * <p>
 * in
 * <code>bukkit.yml</code>: <blockquote><pre>
 *  economy:
 *      preferred: "iConomy"
 * </pre></blockquote>
 * <p>
 * Copyright (C) 2011
 * AOL license &lt;http://aol.nexua.org&gt;
 * <p>
 * For more information about the licensing of this code in EssentialsX, see below:
 * https://gist.github.com/mdcfe/0935441c9573c030c8bd5a2e604aeec3
 *
 * @author Nijikokun &lt;nijikokun@shortmail.com&gt; (@nijikokun)
 */
public final class Methods {
    private static final Set<Method> Methods = new HashSet<>();
    private static final Set<String> Dependencies = new HashSet<>();
    private static final Set<Method> Attachables = new HashSet<>();
    private static String version = null;
    private static boolean self = false;
    private static Method Method = null;
    private static String preferred = "";

    private Methods() {
    }

    /**
     * Implement all methods along with their respective name &amp; class.
     */
    public static void init() {
        if (!Methods.isEmpty()) {
            throw new IllegalStateException("Methods already initialised!");
        }

        addMethod("Vault", new com.earth2me.essentials.register.payment.methods.VaultEco());
    }

    /**
     * Use to reset methods during disable
     */
    public static void reset() {
        version = null;
        self = false;
        Method = null;
        preferred = "";
        Attachables.clear();
    }

    /**
     * Use to get version of Register plugin
     *
     * @return version
     */
    public static String getVersion() {
        return version;
    }

    /**
     * Used by the plugin to setup version
     *
     * @param v version
     */
    public static void setVersion(final String v) {
        version = v;
    }

    /**
     * Returns an array of payment method names that have been loaded through the <code>_init</code> method.
     *
     * @return Set of names of payment methods that are loaded.
     * @see #setMethod(PluginManager)
     */
    public static Set<String> getDependencies() {
        return Dependencies;
    }

    /**
     * Interprets Plugin class data to verify whether it is compatible with an existing payment method to use for
     * payments and other various economic activity.
     *
     * @param plugin Plugin data from bukkit, Internal Class file.
     * @return Method <em>or</em> Null
     */
    public static Method createMethod(final Plugin plugin) {
        for (final Method method : Methods) {
            if (method.isCompatible(plugin)) {
                method.setPlugin(plugin);
                return method;
            }
        }

        return null;
    }

    private static void addMethod(final String name, final Method method) {
        Dependencies.add(name);
        Methods.add(method);
    }

    /**
     * Verifies if Register has set a payment method for usage yet.
     *
     * @return <code>boolean</code>
     * @see #setMethod(PluginManager)
     * @see #checkDisabled(Plugin)
     */
    public static boolean hasMethod() {
        return Method != null;
    }

    /**
     * Checks Plugin Class against a multitude of checks to verify it's usability as a payment method.
     *
     * @param manager the plugin manager for the server
     * @return <code>boolean</code> True on success, False on failure.
     */
    public static boolean setMethod(final PluginManager manager) {
        if (hasMethod()) {
            return true;
        }

        if (self) {
            self = false;
            return false;
        }

        int count = 0;
        boolean match = false;
        Plugin plugin;

        for (final String name : getDependencies()) {
            if (hasMethod()) {
                break;
            }

            plugin = manager.getPlugin(name);
            if (plugin == null || !plugin.isEnabled()) {
                continue;
            }

            final Method current = createMethod(plugin);
            if (current == null) {
                continue;
            }

            if (preferred.isEmpty()) {
                Method = current;
            } else {
                Attachables.add(current);
            }
        }

        if (!preferred.isEmpty()) {
            do {
                if (hasMethod()) {
                    match = true;
                } else {
                    for (final Method attached : Attachables) {
                        if (attached == null) {
                            continue;
                        }

                        if (hasMethod()) {
                            match = true;
                            break;
                        }

                        if (preferred.isEmpty()) {
                            Method = attached;
                        }

                        if (count == 0) {
                            if (preferred.equalsIgnoreCase(attached.getName())) {
                                Method = attached;
                            } else {
                                Method = attached;
                            }
                        }
                    }

                    count++;
                }
            } while (!match);
        }

        return hasMethod();
    }

    /**
     * Sets the preferred economy
     *
     * @return <code>boolean</code>
     */
    public static boolean setPreferred(final String check) {
        if (getDependencies().contains(check)) {
            preferred = check;
            return true;
        }

        return false;
    }

    /**
     * Grab the existing and initialized (hopefully) Method Class.
     *
     * @return <code>Method</code> <em>or</em> <code>Null</code>
     */
    public static Method getMethod() {
        return Method;
    }

    /**
     * Verify is a plugin is disabled, only does this if we there is an existing payment method initialized in
     * Register.
     *
     * @param method Plugin data from bukkit, Internal Class file.
     * @return <code>boolean</code>
     */
    public static boolean checkDisabled(final Plugin method) {
        if (!hasMethod()) {
            return true;
        }

        if (Method.isCompatible(method)) {
            Method = null;
        }

        return Method == null;
    }
}
