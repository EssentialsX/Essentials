package com.earth2me.essentials.register.payment;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.plugin.Plugin;

public class MethodFactory {

    private static Set<Method> Methods = new HashSet<Method>();

    public static Method createMethod(Plugin plugin) {
        for (Method method: Methods) {
            if (method.isCompatible(plugin)) {
                method.setPlugin(plugin);
                return method;
            }
        }

        return null;
    }

    public static void addMethod(Method method) {
        Methods.add(method);
    }
}
