package com.earth2me.essentials.register.payment;

import com.iConomy.iConomy;
import cosine.boseconomy.BOSEconomy;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.PluginDescriptionFile;

public class Methods {
    private Method Method = null;
    public Plugin method = null;
    
    public boolean setMethod(Plugin method) {
        PluginManager loader = method.getServer().getPluginManager();

        if(method.isEnabled()) {
            PluginDescriptionFile info = method.getDescription();
            String name = info.getName();

            if(name.equalsIgnoreCase("iconomy")) {
                if(method.getClass().getName().equals("com.iConomy.iConomy"))
                    Method = new MethodiCo5((iConomy)method);
                else { Method = new MethodiCo4((com.nijiko.coelho.iConomy.iConomy)method); }
            } else if(name.equalsIgnoreCase("boseconomy")) {
                Method = new MethodBOSEconomy((BOSEconomy)method);
            } 
        }
        
        if(!hasMethod()) {
            if(loader.getPlugin("iConomy") != null) {
                method =  loader.getPlugin("iConomy");
                if(method.getClass().getName().equals("com.iConomy.iConomy"))
                    Method = new MethodiCo5((iConomy)method);
                else { Method = new MethodiCo4((com.nijiko.coelho.iConomy.iConomy)method); }
            } else if(loader.getPlugin("BOSEconomy") != null) {
                method = loader.getPlugin("BOSEconomy");
                Method = new MethodBOSEconomy((BOSEconomy)method);
            }
        }
        
        return hasMethod();
    }

    public boolean checkDisabled(Plugin method) {
        PluginDescriptionFile info = method.getDescription();
        String name = info.getName();

        if(name.equalsIgnoreCase("iconomy")) {
            if(method.getClass().getName().equals("com.iConomy.iConomy"))
                Method = null;
            else { Method = null; }
        } else if(name.equalsIgnoreCase("boseconomy")) {
            Method = null;
        } else if(name.equalsIgnoreCase("essentials")) {
            Method = null;
        }

        return (Method == null);
    }

    public boolean hasMethod() {
        return (Method != null);
    }

    public Method getMethod() {
        return Method;
    }

}
