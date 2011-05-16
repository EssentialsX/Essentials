package com.earth2me.essentials.register.payment;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
 
/***
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
        if(hasMethod()) return true;

        PluginManager manager = method.getServer().getPluginManager();
        Plugin plugin = null;

        for(String name: this.getDependencies()) {
            if(hasMethod()) break;
            if(method.getDescription().getName().equals(name)) plugin = method; else  plugin = manager.getPlugin(name);
            if(plugin == null) continue;
            if(!plugin.isEnabled()) continue;

            Method current = this.createMethod(plugin);
            if (current != null) this.Method = current;
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
