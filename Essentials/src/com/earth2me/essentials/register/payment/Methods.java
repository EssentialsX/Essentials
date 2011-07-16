package com.earth2me.essentials.register.payment;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Methods.java
 * Controls the getting / setting of methods & the method of payment used.
 *
 * @author: Nijikokun<nijikokun@gmail.com> (@nijikokun)
 * @copyright: Copyright (C) 2011
 * @license: GNUv3 Affero License <http://www.gnu.org/licenses/agpl-3.0.html>
 */
public class Methods {
    private boolean self = false;
    private Method Method = null;
    private String preferred = "";
    private Set<Method> Methods = new HashSet<Method>();
    private Set<String> Dependencies = new HashSet<String>();
    private Set<Method> Attachables = new HashSet<Method>();

    public Methods() {
        this._init();
    }

    /**
     * Allows you to set which economy plugin is most preferred.
     * 
     * @param preferred - preferred economy plugin
     */
    public Methods(String preferred) {
        this._init();

        if(this.Dependencies.contains(preferred)) {
            this.preferred = preferred;
        }
    }

    private void _init() {
        this.addMethod("iConomy", new com.earth2me.essentials.register.payment.methods.iCo4());
        this.addMethod("iConomy", new com.earth2me.essentials.register.payment.methods.iCo5());
        this.addMethod("BOSEconomy", new com.earth2me.essentials.register.payment.methods.BOSE6());
        this.addMethod("BOSEconomy", new com.earth2me.essentials.register.payment.methods.BOSE7());
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
        if(self) { self = false; return false; }

        int count = 0;
        boolean match = false;
        Plugin plugin;
        PluginManager manager = method.getServer().getPluginManager();

        for(String name: this.getDependencies()) {
            if(hasMethod()) break;
            if(method.getDescription().getName().equals(name)) plugin = method; else  plugin = manager.getPlugin(name);
            if(plugin == null) continue;

            Method current = this.createMethod(plugin);
            if(current == null) continue;

            if(this.preferred.isEmpty())
                this.Method = current;
            else {
                this.Attachables.add(current);
            }
        }

        if(!this.preferred.isEmpty()) {
            do {
                if(hasMethod()) {
                    match = true;
                } else {
                    for(Method attached: this.Attachables) {
                        if(attached == null) continue;

                        if(hasMethod()) {
                            match = true;
                            break;
                        }

                        if(this.preferred.isEmpty()) this.Method = attached;

                        if(count == 0) {
                            if(this.preferred.equalsIgnoreCase(attached.getName()))
                                this.Method = attached;
                        } else {
                            this.Method = attached;
                        }
                    }

                    count++;
                }
            } while(!match);
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
