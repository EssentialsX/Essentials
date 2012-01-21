package com.nijikokun.bukkit.Permissions;

import com.nijiko.permissions.PermissionHandler;
import java.util.logging.Logger;
//import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.permissions.NijikoPermissionsProxy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Permissions extends JavaPlugin {

    public static final Logger log = Logger.getLogger("Fake Permissions");
    public static String name = "Permissions";
    public static String codename = "Hacked Permissions by AnjoCaido";
    public static String version = "2.0";
    public static PermissionHandler Security = null;

    @Override
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("Fake " + pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!");
    }

    @Override
    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();

        if (Security == null) {
            //make sure we have only one instance
            Security = new NijikoPermissionsProxy(null);
        }

        Plugin p = (this.getServer() == null) ? null : this.getServer().getPluginManager().getPlugin("GroupManager");
        if (p != null) {
            if (p.isEnabled()) {
                setGM(p);
            } else {
                if (this.getServer() != null) {
                    this.getServer().getPluginManager().registerEvents(new OverrideListener(this), this);
                }
            }
        } else {
            System.err.println("OOOPS! Fake " + pdfFile.getName() + " version " + pdfFile.getVersion() + " couldn't find GroupManager!");
            this.getPluginLoader().disablePlugin(this);
        }

        if (pdfFile != null) {
            System.out.println("Fake " + pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
        }
    }

    public void setGM(final Plugin p) {
        //GroupManager groupManager = (GroupManager) p;
        ((NijikoPermissionsProxy) Security).setGM(p);
    }

    public PermissionHandler getHandler() {
        if (Security == null) {
            Security = new NijikoPermissionsProxy(null);
        }
        return Security;
    }

    public void setupPermissions() {
        if (Security == null) {
            Security = new NijikoPermissionsProxy(null);
        }
    }
    
    
    
}


