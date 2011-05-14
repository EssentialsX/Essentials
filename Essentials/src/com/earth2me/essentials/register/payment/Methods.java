package com.earth2me.essentials.register.payment;

import org.bukkit.plugin.Plugin;

public class Methods {

    private Method Method = null;

    public boolean setMethod(Plugin method) {
        if (method.isEnabled()) {
            Method plugin = MethodFactory.createMethod(method);
            if (plugin != null) Method = plugin;
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
