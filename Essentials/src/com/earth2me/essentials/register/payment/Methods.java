package com.earth2me.essentials.register.payment;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class Methods {

    private Method Method = null;

    public boolean setMethod(Plugin method) {
        PluginManager manager = method.getServer().getPluginManager();

        if (method != null && method.isEnabled()) {
            Method plugin = MethodFactory.createMethod(method);
            if (plugin != null) Method = plugin;
        } else {
            for(String name: MethodFactory.getDependencies()) {
                if(hasMethod()) break;

                method = manager.getPlugin(name);
                if(method == null) continue;
                if(!method.isEnabled()) manager.enablePlugin(method);
                if(!method.isEnabled()) continue;

                Method plugin = MethodFactory.createMethod(method);
                if (plugin != null) Method = plugin;
            }
        }

        return hasMethod();
    }

    public boolean checkDisabled(Plugin method) {
        if(!hasMethod()) return true;
        if (Method.isCompatible(method)) Method = null;
        return (Method == null);
    }

    public boolean hasMethod() {
        return (Method != null);
    }

    public Method getMethod() {
        return Method;
    }
}
