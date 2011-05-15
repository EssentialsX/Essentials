package com.earth2me.essentials.register.payment;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Methods.java
 * Controls the getting / setting of methods & the method of payment used.
 *
 * @author: Nijikokun<nijikokun@gmail.com> (@nijikokun)
 * @copyright: Copyright (C) 2011
 * @license: GNUv3 Affero License <http://www.gnu.org/licenses/agpl-3.0.html>
 */
public class Methods {

    private Method Method = null;
    private Set<Method> Methods = new HashSet<Method>();
    private Set<String> Dependencies = new HashSet<String>();

    public Methods() {
        this.addMethod("iConomy", new com.earth2me.essentials.register.payment.methods.iCo4());
        this.addMethod("iConomy", new com.earth2me.essentials.register.payment.methods.iCo5());
        this.addMethod("BOSEconomy", new com.earth2me.essentials.register.payment.methods.BOSE());
    }

    public Set<String> getDependencies() {
        return Dependencies;
    }

    public Method createMethod(Plugin plugin) {
        for (Method method: Methods) {
            if (method.isCompatible(plugin)) {
                method.setPlugin(plugin);
                return method;
            }
        }

        return null;
    }

    private void addMethod(String name, Method method) {
        Dependencies.add(name);
        Methods.add(method);
    }

    public boolean hasMethod() {
        return (Method != null);
    }

    public boolean setMethod(Plugin method) {
        PluginManager manager = method.getServer().getPluginManager();

        if (method != null && method.isEnabled()) {
            Method plugin = this.createMethod(method);
            if (plugin != null) Method = plugin;
        } else {
            for(String name: this.getDependencies()) {
                if(hasMethod()) break;

                method = manager.getPlugin(name);
                if(method == null) continue;
                if(!method.isEnabled()) manager.enablePlugin(method);
                if(!method.isEnabled()) continue;

                Method plugin = this.createMethod(method);
                if (plugin != null) Method = plugin;
            }
        }

        return hasMethod();
    }

    public Method getMethod() {
        return Method;
    }

    public boolean checkDisabled(Plugin method) {
        if(!hasMethod()) return true;
        if (Method.isCompatible(method)) Method = null;
        return (Method == null);
    }
}
